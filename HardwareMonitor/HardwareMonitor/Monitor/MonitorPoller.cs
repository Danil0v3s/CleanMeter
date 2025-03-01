#pragma warning disable CS8601 // Possible null

using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;
using HardwareMonitor.PresentMon;
using HardwareMonitor.SharedMemory;
using HardwareMonitor.Sockets;
using LibreHardwareMonitor.Hardware;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace HardwareMonitor.Monitor;

public class MonitorPoller(
    IHostApplicationLifetime hostApplicationLifetime,
    ILogger<MonitorPoller> logger
) : BackgroundService
{
    private readonly Computer _computer = new()
    {
        IsCpuEnabled = true,
        IsGpuEnabled = true,
        IsMemoryEnabled = true,
        IsMotherboardEnabled = true,
        IsControllerEnabled = true,
        IsNetworkEnabled = true,
        IsPsuEnabled = true,
        IsBatteryEnabled = true,
        IsStorageEnabled = true,
    };

    private SocketHost _socketHost = new(logger);
    private readonly PresentMonPoller _presentMonPoller = new(logger);

    private short _pollingRate = 500;
    private const short MinimalPollingRate = 50;

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        logger.LogInformation("Starting monitor");

        _computer.Open();
        _computer.Accept(new UpdateVisitor());
        _presentMonPoller.Start(stoppingToken);
        _presentMonPoller.OnUpdateApps += SendPresentMonAppsToClients;
        _socketHost.StartServer();
        _socketHost.OnClientData += OnClientData;
        _socketHost.OnClientConnected += OnClientConnected;

        var sharedMemoryData = QueryHardwareData();

        using var memoryStream = new MemoryStream();
        using var writer = new BinaryWriter(memoryStream);
        var accumulator = 0;

        WriteDataToStream(writer, sharedMemoryData);

        while (!stoppingToken.IsCancellationRequested)
        {
            if (!_socketHost.HasConnections())
            {
                logger.LogInformation("No clients, skipping...");
                await Task.Delay(1000, stoppingToken);
                continue;
            }

            foreach (var hardware in sharedMemoryData.Hardwares)
            {
                try
                {
                    hardware.Update();
                }
                catch
                {
                    hardware.StopUpdates();
                    logger.LogError("Stopping updates of {HardwareName} - {HardwareIdentifier}", hardware.Name, hardware.Identifier);
                }
            }

            WriteDataToStream(writer, sharedMemoryData);

            if (_socketHost.HasConnections())
            {
                _socketHost.SendToAll(memoryStream.ToArray());
            }

            if (accumulator >= 1000)
            {
                GC.Collect();
                accumulator = 0;
            }

            accumulator += 500;
            await Task.Delay(_pollingRate, stoppingToken);
        }

        Stop();
        hostApplicationLifetime.StopApplication();
    }

    private static void WriteDataToStream(BinaryWriter writer, SharedMemoryData sharedMemoryData)
    {
        writer.Seek(0, SeekOrigin.Begin);
        writer.Write((short)MonitorPacketCommand.Data);
        writer.Write(sharedMemoryData.Hardwares.Count);
        writer.Write(sharedMemoryData.Sensors.Count);

        foreach (var hardware in sharedMemoryData.Hardwares)
        {
            writer.Write((short)hardware.Name.Length);
            writer.Write((short)hardware.Identifier.Length);
            writer.Write(Encoding.UTF8.GetBytes(hardware.Name));
            writer.Write(Encoding.UTF8.GetBytes(hardware.Identifier));
            writer.Write((int)hardware.HardwareType);
        }

        foreach (var sensor in sharedMemoryData.Sensors)
        {
            var value = sensor.HardwareSensor.Value ?? 0f;
            var floatValue = (IsNaN(value) ? 0f : value).ToString(CultureInfo.InvariantCulture);
            sensor.Value = float.Parse(floatValue, CultureInfo.InvariantCulture);

            writer.Write((short)sensor.Name.Length);
            writer.Write((short)sensor.Identifier.Length);
            writer.Write((short)sensor.HardwareIdentifier.Length);
            writer.Write(Encoding.UTF8.GetBytes(sensor.Name));
            writer.Write(Encoding.UTF8.GetBytes(sensor.Identifier));
            writer.Write(Encoding.UTF8.GetBytes(sensor.HardwareIdentifier));
            writer.Write((int)sensor.SensorType);
            writer.Write((float)sensor.Value);
        }
    }

    private void OnClientConnected()
    {
        SendPresentMonAppsToClients();
    }

    private void OnClientData(byte[] data)
    {
        var cmd = (MonitorPacketCommand)BitConverter.ToInt16(data, 0);
        logger.LogInformation("Received command from client: {Command}", cmd);
        switch (cmd)
        {
            case MonitorPacketCommand.RefreshPresentMonApps:
                SendPresentMonAppsToClients();
                break;
            case MonitorPacketCommand.SelectPresentMonApp:
                SelectPresentMonApp(data);
                break;
            case MonitorPacketCommand.SelectPollingRate:
                SelectPollingRate(data);
                break;

            // server -> client cases 
            case MonitorPacketCommand.Data:
            case MonitorPacketCommand.PresentMonApps:
                break;
            default:
                throw new ArgumentOutOfRangeException();
        }
    }

    private void SelectPollingRate(byte[] data)
    {
        // start at 2 because the first 2 were the command
        var pollingRate = BitConverter.ToInt16(data, 2);
        _pollingRate = Math.Max(pollingRate, MinimalPollingRate);
        logger.LogInformation("Selected polling rate of {PollingRate}", _pollingRate);
    }

    private void SelectPresentMonApp(byte[] data)
    {
        // start at 2 because the first 2 were the command
        var size = BitConverter.ToInt16(data, 2);
        var appName = Encoding.UTF8.GetString(data, 4, size);
        _presentMonPoller.SetSelectedApp(appName);
    }

    private void SendPresentMonAppsToClients()
    {
        using var memoryStream = new MemoryStream();
        using var writer = new BinaryWriter(memoryStream);

        writer.Write((short)MonitorPacketCommand.PresentMonApps);
        writer.Write((short)_presentMonPoller.CurrentApps.Count);
        foreach (var app in _presentMonPoller.CurrentApps)
        {
            writer.Write(GetBytes(app, SharedMemoryConsts.NameSize));
        }

        if (_socketHost.HasConnections())
        {
            _socketHost.SendToAll(memoryStream.ToArray());
        }
        else
        {
            logger.LogInformation("No clients to send");
        }
    }

    private SharedMemoryData QueryHardwareData()
    {
        var hardwareList = new List<SharedMemoryHardware>();
        var sensorList = new List<SharedMemorySensor>();
        var sharedMemoryData = new SharedMemoryData();

        foreach (var hardware in _computer.Hardware)
        {
            hardwareList.Add(MapHardware(hardware));
            foreach (var subHardware in hardware.SubHardware)
            {
                hardwareList.Add(MapHardware(subHardware));
            }
        }

        foreach (var hardware in _computer.Hardware)
        {
            foreach (var sensor in hardware.Sensors)
            {
                sensor.ValuesTimeWindow = TimeSpan.Zero;
                sensorList.Add(MapSensor(sensor));
            }

            foreach (var subHardware in hardware.SubHardware)
            {
                foreach (var sensor in subHardware.Sensors)
                {
                    sensor.ValuesTimeWindow = TimeSpan.Zero;
                    sensorList.Add(MapSensor(sensor));
                }
            }
        }

        sensorList.Add(MapSensor(_presentMonPoller.Displayed));
        sensorList.Add(MapSensor(_presentMonPoller.Presented));
        sensorList.Add(MapSensor(_presentMonPoller.Frametime));

        sharedMemoryData.Sensors = sensorList;
        sharedMemoryData.Hardwares = hardwareList;

        return sharedMemoryData;
    }

    private void Stop()
    {
        _computer.Close();
        _presentMonPoller.Stop();
        _socketHost.Close();
        _socketHost.OnClientData -= OnClientData;
    }

    private static SharedMemoryHardware MapHardware(IHardware hardware) => new()
    {
        Name = RemoveSpecialCharacters(hardware.Name),
        Identifier = hardware.Identifier.ToString(),
        HardwareType = hardware.HardwareType,
        Hardware = hardware
    };

    private static SharedMemorySensor MapSensor(ISensor sensor) => new()
    {
        Name = RemoveSpecialCharacters(sensor.Name),
        Identifier = sensor.Identifier.ToString(),
        SensorType = sensor.SensorType,
        Value = float.IsNaN(sensor.Value ?? 0f) ? 0f : (sensor.Value ?? 0f),
        HardwareIdentifier = sensor.Hardware.Identifier.ToString(),
        HardwareSensor = sensor
    };

    private static byte[] GetBytes(string str, int length)
    {
        return Encoding.UTF8.GetBytes(str.Length > length ? str[..length] : str.PadRight(length, '\0'));
    }

    public static string RemoveSpecialCharacters(string str)
    {
        return Regex.Replace(str, "[^a-zA-Z0-9_ .]+", "_", RegexOptions.Compiled);
    }

    public static unsafe bool IsNaN(float f)
    {
        int binary = *(int*)(&f);
        return ((binary & 0x7F800000) == 0x7F800000) && ((binary & 0x007FFFFF) != 0);
    }
}