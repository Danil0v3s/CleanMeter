// See https://aka.ms/new-console-template for more information

using HardwareMonitor.Monitor;
using HardwareMonitor.PresentMon;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Serilog;

// Diagnostic: `HardwareMonitor.exe --pm-probe <process[.exe]>` drives the real
// PresentMon poller directly (no host/pipe) and prints live metrics, so the
// .NET <-> PresentMon service path can be validated without the Tauri UI.
// Diagnostic: `HardwareMonitor.exe --pm-probe-pid <pid> [pid...]` tracks raw PIDs
// (bypassing name->PID resolution) and reports per-PID swap-chain counts, so we can
// find which process in a multi-process app (e.g. Chromium) actually presents.
if (args.Length >= 2 && args[0] == "--pm-probe-pid")
{
    var pids = args.Skip(1).Select(uint.Parse).ToArray();
    await ProbePresentMonPids(pids);
    return;

    static async Task ProbePresentMonPids(uint[] pids)
    {
        PresentMonApi.pmLoaderSetPathToMiddlewareDll_(
            Path.Combine(AppContext.BaseDirectory, "PresentMonAPI2.dll"));

        if (PresentMonApi.pmOpenSession(out var session) != PresentMonApi.PM_STATUS_SUCCESS)
        {
            Console.WriteLine("pmOpenSession failed (is PresentMonSharedService running?)");
            return;
        }

        var elements = new[]
        {
            new PresentMonApi.PM_QUERY_ELEMENT { metric = PresentMonApi.PM_METRIC.PM_METRIC_PRESENTED_FRAME_TIME, stat = PresentMonApi.PM_STAT.PM_STAT_AVG },
            new PresentMonApi.PM_QUERY_ELEMENT { metric = PresentMonApi.PM_METRIC.PM_METRIC_PRESENTED_FPS,        stat = PresentMonApi.PM_STAT.PM_STAT_AVG },
        };
        PresentMonApi.pmRegisterDynamicQuery(session, out var query, elements, (ulong)elements.Length, 1000.0, 0.0);
        var stride = (int)(elements[^1].dataOffset + elements[^1].dataSize);
        var blob = new byte[stride * 8];

        foreach (var pid in pids)
        {
            var st = PresentMonApi.pmStartTrackingProcess(session, pid);
            Console.WriteLine($"track pid {pid} -> status {st}");
        }

        using var cts = new CancellationTokenSource();
        Console.CancelKeyPress += (_, e) => { e.Cancel = true; cts.Cancel(); };

        try
        {
            while (!cts.IsCancellationRequested)
            {
                foreach (var pid in pids)
                {
                    uint n = 8;
                    var st = PresentMonApi.pmPollDynamicQuery(query, pid, blob, ref n);
                    if (st != PresentMonApi.PM_STATUS_SUCCESS) { Console.WriteLine($"pid {pid}: poll status {st}"); continue; }
                    if (n == 0) { Console.WriteLine($"pid {pid}: no swapchains"); continue; }
                    var ft = BitConverter.ToDouble(blob, (int)elements[0].dataOffset);
                    var pfps = BitConverter.ToDouble(blob, (int)elements[1].dataOffset);
                    Console.WriteLine($"pid {pid}: swapchains={n}  frametime={ft,7:F2} ms  presentedFps={pfps,7:F1}");
                }
                Console.WriteLine("----");
                await Task.Delay(800, cts.Token);
            }
        }
        catch (OperationCanceledException) { }
        finally
        {
            PresentMonApi.pmFreeDynamicQuery(query);
            foreach (var pid in pids) PresentMonApi.pmStopTrackingProcess(session, pid);
            PresentMonApi.pmCloseSession(session);
        }
    }
}

if (args.Length >= 2 && args[0] == "--pm-probe")
{
    await ProbePresentMon(args[1]);
    return;

    static async Task ProbePresentMon(string processName)
    {
        using var loggerFactory = LoggerFactory.Create(b => b.AddSimpleConsole());
        var log = loggerFactory.CreateLogger("pm-probe");

        var poller = new PresentMonPoller(log);
        poller.SetSelectedApp(processName); // force target (bypasses foreground pipe)

        using var cts = new CancellationTokenSource();
        Console.CancelKeyPress += (_, e) => { e.Cancel = true; cts.Cancel(); };

        poller.Start(cts.Token);
        Console.WriteLine($"Probing PresentMon for \"{processName}\" (Ctrl+C to stop)...");

        try
        {
            while (!cts.IsCancellationRequested)
            {
                var ft = poller.Frametime.Value ?? 0f;
                var fps = ft > 0 ? 1000f / ft : 0f;
                Console.WriteLine(
                    $"frametime={ft,7:F2} ms   fps(1000/ft)={fps,7:F1}   " +
                    $"presentedFps={poller.Presented.Value ?? 0f,7:F1}   " +
                    $"displayedFps={poller.Displayed.Value ?? 0f,7:F1}");
                await Task.Delay(500, cts.Token);
            }
        }
        catch (OperationCanceledException) { /* Ctrl+C */ }
        finally
        {
            poller.Stop();
        }
    }
}

var builder = Host.CreateDefaultBuilder(args)
    .ConfigureServices(services => 
    {
        services.AddHostedService<MonitorPoller>();
        services.Configure<HostOptions>(options =>
        {
            options.ShutdownTimeout = TimeSpan.FromSeconds(30);
        });
    })
    .UseWindowsService(options =>
    {
        options.ServiceName = "CleanMeter Hardware Monitor";
    })
    .ConfigureLogging((context, logging) =>
    {
        logging.ClearProviders();
        if (Environment.UserInteractive)
        {
            logging.AddConsole();
        }
        logging.AddEventLog();
    })
    .UseSerilog((context, services, loggerConfiguration) => 
    {
        var logPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "LogFiles");
        Directory.CreateDirectory(logPath);
        
        loggerConfiguration
            .ReadFrom.Configuration(context.Configuration)
            .ReadFrom.Services(services)
            .Enrich.FromLogContext()
            .WriteTo.File(
                Path.Combine(logPath, "cleanmeter-hardware-monitor-.log"),
                rollingInterval: RollingInterval.Day,
                retainedFileCountLimit: 30,
                outputTemplate: "[{Timestamp:yyyy-MM-dd HH:mm:ss.fff}] [{Level}] {Message}{NewLine}{Exception}");
                
        // Only log to console when running interactively (not as service)
        if (Environment.UserInteractive)
        {
            loggerConfiguration.WriteTo.Console();
        }
    });

var host = builder.Build();

// Handle Windows shutdown signals
var lifetime = host.Services.GetRequiredService<IHostApplicationLifetime>();
var logger = host.Services.GetRequiredService<ILogger<Program>>();

lifetime.ApplicationStopping.Register(() =>
{
    logger.LogInformation("Application shutdown signal received");
});

// Handle console cancel events (Ctrl+C) only when running interactively
if (Environment.UserInteractive)
{
    Console.CancelKeyPress += (sender, e) =>
    {
        logger.LogInformation("Console cancel event received");
        e.Cancel = true;
        lifetime.StopApplication();
    };
}

try
{
    logger.LogInformation("Starting CleanMeter Hardware Monitor Service");
    await host.RunAsync();
}
catch (Exception ex)
{
    logger.LogCritical(ex, "Application terminated unexpectedly");
    throw;
}
finally
{
    logger.LogInformation("CleanMeter Hardware Monitor Service stopped");
}