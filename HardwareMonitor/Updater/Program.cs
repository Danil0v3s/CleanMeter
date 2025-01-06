// See https://aka.ms/new-console-template for more information

using System.Diagnostics;
using System.IO.Compression;

var arguments = Environment.GetCommandLineArgs();

new Updater().Update(arguments);

class Updater
{
    public void Update(string[] arguments)
    {
        var dict = ParseArguments(arguments);
        if (!dict.ContainsKey("--package") || !dict.ContainsKey("--path") || !dict.ContainsKey("--autostart")) return;
        var isAutostart = dict["--autostart"] == "true";
        if (!isAutostart)
        {
            ChangeService("stop");
            // DeleteService();
        }

        RenameCurrentExecutable();
        UnzipPackage(dict["--package"], dict["--path"]);
        LaunchApp(isAutostart, dict["--path"]);
        Environment.Exit(0);
    }

    private void ChangeService(string action)
    {
        var processStartInfo = new ProcessStartInfo
        {
            CreateNoWindow = true,
            RedirectStandardOutput = true,
            UseShellExecute = false,
            FileName = "cmd.exe",
            Arguments = $"/c sc {action} svcleanmeter"
        };
        var process = new Process();
        process.StartInfo = processStartInfo;
        process.Start();
    }

    private void RenameCurrentExecutable()
    {
        var currentPath = Environment.ProcessPath!;
        var newPath = Path.ChangeExtension(currentPath, ".bak");
        File.Move(currentPath, newPath, true);
    }

    private void UnzipPackage(string package, string destination)
    {
        ZipFile.ExtractToDirectory(package, $@"{destination}\..\", true);
    }

    private void LaunchApp(bool isAutostart, string path)
    {
        if (isAutostart)
        {
            ChangeService("start");
        }

        var processStartInfo = new ProcessStartInfo
        {
            CreateNoWindow = true,
            RedirectStandardOutput = true,
            UseShellExecute = false,
            FileName = "cmd.exe",
            Arguments = $"/c {path}\\cleanmeter.exe"
        };
        var process = new Process();
        process.StartInfo = processStartInfo;
        process.Start();
    }

    private Dictionary<string, string> ParseArguments(string[] args)
    {
        var arguments = new Dictionary<string, string>();

        foreach (var argument in args)
        {
            var splitted = argument.Split('=');

            if (splitted.Length == 2)
            {
                arguments[splitted[0]] = splitted[1];
            }
        }

        return arguments;
    }
}