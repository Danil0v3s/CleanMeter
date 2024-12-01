using System.Diagnostics;
using System.Globalization;
using LibreHardwareMonitor.Hardware;

namespace HardwareMonitor.PresentMon;

public class PresentMonPoller
{
    private IHardware _hardware = new PresentMonHardware();
    public PresentMonSensor Displayed { get; private set; }
    public PresentMonSensor Presented { get; private set; }
    public PresentMonSensor Frametime { get; private set; }

    private Process _process;
    private CultureInfo _cultureInfo = (CultureInfo)CultureInfo.CurrentCulture.Clone();

    public async void Start()
    {
        _cultureInfo.NumberFormat.NumberDecimalSeparator = ".";

        Displayed = new PresentMonSensor(_hardware, "displayed", 0, "Displayed Frames");
        Presented = new PresentMonSensor(_hardware, "presented", 1, "Presented Frames");
        Frametime = new PresentMonSensor(_hardware, "frametime", 2, "Frametime");
        
        using var reader = new StreamReader(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "presentmon", "ignored-processes.txt"));
        var text = await reader.ReadToEndAsync();
        var processes = text
            .Split("\n", StringSplitOptions.RemoveEmptyEntries)
            .Select(x => $"--exclude {x.Trim()}");

        var processStartInfo = new ProcessStartInfo
        {
            CreateNoWindow = true,
            RedirectStandardOutput = true,
            UseShellExecute = false,
            FileName = "presentmon\\presentmon.exe",
            Arguments = $"--stop_existing_session --no_console_stats --output_stdout {string.Join(' ', processes)}"
        };
        _process = new Process();
        _process.StartInfo = processStartInfo;

        _process.OutputDataReceived += (sender, args) => ParseData(args.Data);
        _process.Exited += (sender, args) => Start();
        
        _process.Start();
        _process.BeginOutputReadLine();

        await _process.WaitForExitAsync();
    }

    public void Stop()
    {
        _process.Kill(true);
    }

    private void ParseData(string? argsData)
    {
        string[] parts;
        if (argsData != null)
        {
            parts = argsData.Split(",");
            if (float.TryParse(parts[9], NumberStyles.Any, _cultureInfo, out var frametime))
            {
                Frametime.Value = frametime;
            }

            if (float.TryParse(parts[13], NumberStyles.Any, _cultureInfo, out var gpuTime))
            {
                Presented.Value = gpuTime;
            }

            if (float.TryParse(parts[17], NumberStyles.Any, _cultureInfo, out var displayed))
            {
                Displayed.Value = displayed;
            }
        }
    }
}