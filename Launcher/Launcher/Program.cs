using System.Diagnostics;

try
{
    var startInfo = new ProcessStartInfo("cleanmeter\\cleanmeter.exe")
    {
        Verb = "runas",
        UseShellExecute = true,
        WindowStyle = ProcessWindowStyle.Hidden,
        CreateNoWindow = false,
    };
    Process.Start(startInfo);
}
catch (Exception ex)
{
    Console.WriteLine(ex.Message);
}