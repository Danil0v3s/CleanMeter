using System.IO.MemoryMappedFiles;
using System.Text;
using HardwareMonitor.PresentMon;
using HardwareMonitor.SharedMemory;
using LibreHardwareMonitor.Hardware;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace HardwareMonitor.Monitor;

public class MonitorPoller(IHostApplicationLifetime hostApplicationLifetime,
    ILogger<MonitorPoller> logger) : BackgroundService
{
    Computer _computer = new()
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

    PresentMonPoller _presentMonPoller = new();

    private bool _isOpen;

    private void Stop()
    {
        _computer.Close();
        _presentMonPoller.Stop();
    }

    private static SharedMemoryHardware MapHardware(IHardware hardware) => new()
    {
        Name = hardware.Name,
        Identifier = hardware.Identifier.ToString(),
        HardwareType = hardware.HardwareType,
        Hardware = hardware
    };

    private static SharedMemorySensor MapSensor(ISensor sensor) => new()
    {
        Name = sensor.Name,
        Identifier = sensor.Identifier.ToString(),
        SensorType = sensor.SensorType,
        Value = float.IsNaN(sensor.Value ?? 0f) ? 0f : (sensor.Value ?? 0f),
        HardwareIdentifier = sensor.Hardware.Identifier.ToString(),
        Sensor = sensor
    };

    private static byte[] GetBytes(string str, int length)
    {
        return Encoding.UTF8.GetBytes(str.Length > length ? str[..length] : str.PadRight(length, '\0'));
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        logger.LogInformation("Starting monitor");
        
        _isOpen = true;
        _computer.Open();
        _computer.Accept(new UpdateVisitor());
        _presentMonPoller.Start();

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

        var sensorValueOffset = new Dictionary<int, int>();
        using var memoryMappedFile = MemoryMappedFile.CreateNew(SharedMemoryConsts.SharedMemoryName, 500_000);
        using var mmfStream = memoryMappedFile.CreateViewStream();
        using var writer = new BinaryWriter(mmfStream);
        var accumulator = 0;

        writer.Write(hardwareList.Count);
        writer.Write(sensorList.Count);

        foreach (var hardware in hardwareList)
        {
            writer.Write(GetBytes(hardware.Name, SharedMemoryConsts.NameSize));
            writer.Write(GetBytes(hardware.Identifier, SharedMemoryConsts.IdentifierSize));
            writer.Write((int)hardware.HardwareType);
        }

        for (var index = 0; index < sensorList.Count; index++)
        {
            var sensor = sensorList[index];
            sensor.Value = float.IsNaN(sensor.Sensor.Value ?? 0f) ? 0f : (sensor.Sensor.Value ?? 0f);

            writer.Write(GetBytes(sensor.Name, SharedMemoryConsts.NameSize));
            writer.Write(GetBytes(sensor.Identifier, SharedMemoryConsts.IdentifierSize));
            writer.Write(GetBytes(sensor.HardwareIdentifier, SharedMemoryConsts.IdentifierSize));
            writer.Write((int)sensor.SensorType);
            writer.Write((float)sensor.Value);
            
            // store the starting offset of the float we just wrote
            sensorValueOffset[index] = (int)writer.BaseStream.Position - 4;
        }

        while (!stoppingToken.IsCancellationRequested)
        {
            foreach (var hardware in hardwareList)
            {
                hardware.Hardware.Update();
            }

            for (var index = 0; index < sensorList.Count; index++)
            {
                var sensor = sensorList[index];
                sensor.Value = float.IsNaN(sensor.Sensor.Value ?? 0f) ? 0f : (sensor.Sensor.Value ?? 0f);
                
                // seek to the sensor value offset
                writer.Seek(sensorValueOffset[index], SeekOrigin.Begin);
                writer.Write((float)sensor.Value);
            }

            if (accumulator >= 1000)
            {
                GC.Collect();
                accumulator = 0;
            }

            accumulator += 500;
            await Task.Delay(500, stoppingToken);
        }
        
        Stop();
        hostApplicationLifetime.StopApplication();
    }
}