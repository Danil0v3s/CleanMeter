// See https://aka.ms/new-console-template for more information

using HardwareMonitor.Monitor;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Serilog;

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