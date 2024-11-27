// See https://aka.ms/new-console-template for more information

using HardwareMonitor.Monitor;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

var builder = Host.CreateDefaultBuilder(args)
    .ConfigureServices(services => services.AddHostedService<MonitorPoller>())
    .UseWindowsService();

var host = builder.Build();
host.Run();