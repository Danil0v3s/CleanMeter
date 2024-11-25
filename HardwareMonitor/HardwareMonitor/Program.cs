// See https://aka.ms/new-console-template for more information

using HardwareMonitor.Monitor;
using LibreHardwareMonitor.Hardware;

var poller = new MonitorPoller();
AppDomain.CurrentDomain.ProcessExit += (s, e) =>
{
    Console.WriteLine("Exit");
    poller.Stop();
};
await poller.Start();