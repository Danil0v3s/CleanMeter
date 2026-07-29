using System.Diagnostics;
using LibreHardwareMonitor.Hardware;
using Microsoft.Extensions.Logging;
using static HardwareMonitor.PresentMon.PresentMonApi;

// ReSharper disable FieldCanBeMadeReadOnly.Local
#pragma warning disable CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider adding the 'required' modifier or declaring as nullable.

namespace HardwareMonitor.PresentMon;

/// <summary>
/// Sources FPS/frame-time data from PresentMon via its client API instead of
/// spawning presentmon.exe and parsing CSV. We connect to PresentMonSharedService,
/// ask it to track the foreground (or user-selected) process, and read a windowed
/// average out of shared memory.
///
/// Public surface is unchanged from the old CSV poller (Displayed/Presented/
/// Frametime/CurrentApps/OnUpdateApps/Start/Stop/SetSelectedApp/
/// SetForegroundApplication) so MonitorPoller and the HardwareMonitor pipe API are
/// untouched.
/// </summary>
public class PresentMonPoller(ILogger logger)
{
    private const string NO_SELECTED_APP = "NONE";

    // Overlay math is fps = 1000 / frametime, and the frame-time graph reads the same
    // sensor (app-tauri/src/lib/model/hardwareMonitorData.ts). So Frametime is the one
    // value that must be correct; we take the presented frame time (ms) averaged over
    // the window. Presented/Displayed FPS are published for parity but currently unused
    // by the overlay. NOTE: if measured FPS ever looks off, this is the metric to swap
    // (e.g. DISPLAYED_FRAME_TIME / BETWEEN_PRESENTS).
    private static readonly PM_QUERY_ELEMENT[] QueryTemplate =
    [
        new() { metric = PM_METRIC.PM_METRIC_PRESENTED_FRAME_TIME, stat = PM_STAT.PM_STAT_AVG },
        new() { metric = PM_METRIC.PM_METRIC_PRESENTED_FPS,        stat = PM_STAT.PM_STAT_AVG },
        new() { metric = PM_METRIC.PM_METRIC_DISPLAYED_FPS,        stat = PM_STAT.PM_STAT_AVG },
    ];

    private const double WindowMs = 1000.0;
    private const int MaxSwapChains = 8;
    private const int PollIntervalMs = 250;
    private const int ReconnectDelayMs = 2000;
    private const int AppsBroadcastIntervalMs = 10_000;

    private readonly IHardware _hardware = new PresentMonHardware();
    public PresentMonSensor Displayed { get; private set; }
    public PresentMonSensor Presented { get; private set; }
    public PresentMonSensor Frametime { get; private set; }
    public HashSet<string> CurrentApps { get; private set; }

    public Action OnUpdateApps;

    private string _currentSelectedApp = NO_SELECTED_APP;
    private string _currentForegroundApp;

    private CancellationTokenSource? _cts;
    private Task? _worker;

    public void Start(CancellationToken stoppingToken)
    {
        Displayed = new PresentMonSensor(_hardware, "displayed", 0, "Displayed Frames");
        Presented = new PresentMonSensor(_hardware, "presented", 1, "Presented Frames");
        Frametime = new PresentMonSensor(_hardware, "frametime", 2, "Frametime");
        CurrentApps = [];

        _cts = CancellationTokenSource.CreateLinkedTokenSource(stoppingToken);
        var token = _cts.Token;
        _worker = Task.Run(() => RunAsync(token), token);
    }

    public void Stop()
    {
        try
        {
            _cts?.Cancel();
            // Give the worker a moment to release the session/tracking cleanly.
            _worker?.Wait(TimeSpan.FromSeconds(2));
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error stopping PresentMon poller");
        }
        finally
        {
            _cts?.Dispose();
            _cts = null;
        }
    }

    public void SetSelectedApp(string appName)
    {
        _currentSelectedApp = appName == "Auto" ? NO_SELECTED_APP : appName;
    }

    public void SetForegroundApplication(string appName)
    {
        _currentForegroundApp = appName;
    }

    /// <summary>
    /// Outer loop: (re)connect to the service, then poll until something breaks.
    /// The service is installed/started on demand by the user, so being unable to
    /// connect is an expected, recoverable state — we just keep retrying.
    /// </summary>
    private async Task RunAsync(CancellationToken token)
    {
        try
        {
            // Pin middleware resolution to the DLL we ship, independent of the registry.
            var middleware = Path.Combine(AppContext.BaseDirectory, "PresentMonAPI2.dll");
            pmLoaderSetPathToMiddlewareDll_(middleware);
        }
        catch (DllNotFoundException)
        {
            logger.LogError("PresentMonAPI2Loader.dll not found next to HardwareMonitor.exe; FPS data disabled");
            return;
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Failed to set PresentMon middleware path");
        }

        while (!token.IsCancellationRequested)
        {
            IntPtr session = IntPtr.Zero;
            IntPtr query = IntPtr.Zero;
            try
            {
                var status = pmOpenSession(out session);
                if (status != PM_STATUS_SUCCESS)
                {
                    session = IntPtr.Zero;
                    await Task.Delay(ReconnectDelayMs, token);
                    continue;
                }

                // Register the query; the API fills in per-element dataOffset/dataSize.
                var elements = (PM_QUERY_ELEMENT[])QueryTemplate.Clone();
                status = pmRegisterDynamicQuery(session, out query, elements,
                    (ulong)elements.Length, WindowMs, 0.0);
                if (status != PM_STATUS_SUCCESS)
                {
                    logger.LogError("pmRegisterDynamicQuery failed ({Status})", status);
                    query = IntPtr.Zero;
                    await Task.Delay(ReconnectDelayMs, token);
                    continue;
                }

                var last = elements[^1];
                var blobStride = (int)(last.dataOffset + last.dataSize);
                logger.LogInformation("Connected to PresentMon service (blob stride {Stride} bytes)", blobStride);

                await PollLoop(session, query, elements, blobStride, token);
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception ex)
            {
                logger.LogError(ex, "PresentMon session error; will reconnect");
            }
            finally
            {
                if (query != IntPtr.Zero) SafeCall(() => pmFreeDynamicQuery(query));
                if (session != IntPtr.Zero)
                {
                    if (_trackedPid != 0) SafeCall(() => pmStopTrackingProcess(session, _trackedPid));
                    SafeCall(() => pmCloseSession(session));
                }
                _trackedPid = 0;
            }

            if (!token.IsCancellationRequested)
                await Task.Delay(ReconnectDelayMs, token);
        }
    }

    private uint _trackedPid;

    /// <summary>Inner loop: resolve the target PID, keep it tracked, poll, publish.</summary>
    private async Task PollLoop(IntPtr session, IntPtr query, PM_QUERY_ELEMENT[] elements,
        int blobStride, CancellationToken token)
    {
        var blob = new byte[blobStride * MaxSwapChains];
        var lastAppsBroadcast = Environment.TickCount64;
        var lastTargetName = string.Empty;

        while (!token.IsCancellationRequested)
        {
            // Manual selection wins; otherwise follow the foreground app ("Auto").
            var targetName = _currentSelectedApp != NO_SELECTED_APP
                ? _currentSelectedApp
                : _currentForegroundApp;

            var targetPid = ResolvePid(targetName);

            if (targetPid != _trackedPid)
            {
                if (_trackedPid != 0)
                    SafeCall(() => pmStopTrackingProcess(session, _trackedPid));

                if (targetPid != 0)
                {
                    var st = pmStartTrackingProcess(session, targetPid);
                    if (st != PM_STATUS_SUCCESS && st != PM_STATUS_ALREADY_TRACKING_PROCESS)
                        logger.LogWarning("pmStartTrackingProcess({Pid}) -> {Status}", targetPid, st);
                }

                _trackedPid = targetPid;
                lastTargetName = targetName ?? string.Empty;
            }

            if (targetPid != 0)
            {
                uint numSwapChains = MaxSwapChains;
                var st = pmPollDynamicQuery(query, targetPid, blob, ref numSwapChains);
                if (st != PM_STATUS_SUCCESS)
                    throw new InvalidOperationException($"pmPollDynamicQuery failed ({st})");

                if (numSwapChains > 0)
                {
                    // Swap chain 0 lives at the start of the blob; read each metric at
                    // the offset the register call assigned it.
                    var frametimeMs = BitConverter.ToDouble(blob, (int)elements[0].dataOffset);
                    var presentedFps = BitConverter.ToDouble(blob, (int)elements[1].dataOffset);
                    var displayedFps = BitConverter.ToDouble(blob, (int)elements[2].dataOffset);

                    if (double.IsFinite(frametimeMs) && frametimeMs > 0)
                    {
                        Frametime.Value = (float)frametimeMs;
                        Presented.Value = (float)presentedFps;
                        Displayed.Value = (float)displayedFps;

                        if (!string.IsNullOrEmpty(lastTargetName))
                            CurrentApps.Add(lastTargetName);
                    }
                }
            }

            // Refresh the client-facing app list on the same 10s cadence as before.
            if (Environment.TickCount64 - lastAppsBroadcast >= AppsBroadcastIntervalMs)
            {
                OnUpdateApps?.Invoke();
                CurrentApps.Clear();
                lastAppsBroadcast = Environment.TickCount64;
            }

            await Task.Delay(PollIntervalMs, token);
        }
    }

    /// <summary>
    /// Resolves an exe name (e.g. "game.exe") to a PID, preferring a process that owns
    /// a top-level window — the one actually presenting frames.
    /// </summary>
    private static uint ResolvePid(string appName)
    {
        if (string.IsNullOrEmpty(appName))
            return 0;

        var bare = appName.EndsWith(".exe", StringComparison.OrdinalIgnoreCase)
            ? appName[..^4]
            : appName;

        Process[] procs;
        try
        {
            procs = Process.GetProcessesByName(bare);
        }
        catch
        {
            return 0;
        }

        try
        {
            var windowed = procs.FirstOrDefault(p => SafeMainWindow(p) != IntPtr.Zero);
            var chosen = windowed ?? procs.FirstOrDefault();
            return chosen == null ? 0u : (uint)chosen.Id;
        }
        finally
        {
            foreach (var p in procs) p.Dispose();
        }
    }

    private static IntPtr SafeMainWindow(Process p)
    {
        try { return p.MainWindowHandle; }
        catch { return IntPtr.Zero; }
    }

    private static void SafeCall(Func<int> action)
    {
        try { action(); }
        catch { /* best-effort cleanup / teardown */ }
    }
}
