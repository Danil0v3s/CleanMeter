// See https://aka.ms/new-console-template for more information

using HardwareMonitor.Monitor;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Serilog;

var builder = Host.CreateDefaultBuilder(args)
    .ConfigureServices(services => services.AddHostedService<MonitorPoller>())
    .UseWindowsService()
    .UseSerilog((context, services, loggerConfiguration) => loggerConfiguration
        .ReadFrom.Configuration(context.Configuration)
        .ReadFrom.Services(services)
        .Enrich.FromLogContext()
        .WriteTo.File(
            Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "LogFiles",
                $"{DateTime.Now.Year}-{DateTime.Now.Month}-{DateTime.Now.Day}", "Log.txt"),
            rollingInterval: RollingInterval.Infinite,
            outputTemplate: "[{Timestamp:yyyy-MM-dd HH:mm:ss.fff} [{Level}] {Message}{NewLine}{Exception}")
        .WriteTo.Console()
    );

var host = builder.Build();
host.Run();