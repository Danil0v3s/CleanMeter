using System.IO.MemoryMappedFiles;
using HardwareMonitor.SharedMemory;
using LibreHardwareMonitor.Hardware;

namespace HardwareMonitor.Monitor;

public class MonitorPoller
{
    Computer computer = new()
    {
        IsCpuEnabled = true,
        IsGpuEnabled = true,
        IsMemoryEnabled = true,
        IsMotherboardEnabled = true,
        IsControllerEnabled = true,
        IsNetworkEnabled = true,
        IsPsuEnabled = true,
        IsBatteryEnabled = true,
        IsStorageEnabled = true
    };

    private bool _isOpen;

    public async ValueTask Start()
    {
        _isOpen = true;
        computer.Open();
        computer.Accept(new UpdateVisitor());

        var hardwareList = new List<SharedMemoryHardware>();
        var sensorList = new List<SharedMemorySensor>();
        var sharedMemoryData = new SharedMemoryData();
        var jsonArray = Array.Empty<byte>();

        foreach (var hardware in computer.Hardware)
        {
            hardwareList.Add(MapHardware(hardware));
            foreach (var subHardware in hardware.SubHardware)
            {
                hardwareList.Add(MapHardware(subHardware));
            }
        }

        foreach (var hardware in computer.Hardware)
        {
            foreach (var sensor in hardware.Sensors)
            {
                sensorList.Add(MapSensor(sensor));
            }

            foreach (var subHardware in hardware.SubHardware)
            {
                foreach (var sensor in subHardware.Sensors)
                {
                    sensorList.Add(MapSensor(sensor));
                }
            }
        }

        sharedMemoryData.Sensors = sensorList;
        sharedMemoryData.Hardwares = hardwareList;

        using var memoryMappedFile = MemoryMappedFile.CreateNew(SharedMemoryConsts.SharedMemoryName, 500_000);
        using var mmfStream = memoryMappedFile.CreateViewStream();
        using var writer = new BinaryWriter(mmfStream);
        var accumulator = 0;
        
        while (_isOpen)
        {
            sharedMemoryData.LastPollTime = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;

            foreach (var sensor in sensorList)
            {
                sensor.Value = float.IsNaN(sensor.Sensor.Value ?? 0f) ? 0f : (sensor.Sensor.Value ?? 0f);
            }

            jsonArray = Utf8Json.JsonSerializer.Serialize(sharedMemoryData);
            writer.Write((int)jsonArray.Length);
            writer.Write(jsonArray);

            if (accumulator >= 120_000)
            {
                GC.Collect();
                accumulator = 0;
            }

            writer.Seek(0, SeekOrigin.Begin);
            accumulator += 500;
            await Task.Delay(500);
        }
    }

    public void Stop()
    {
        computer.Close();
    }

    private SharedMemoryHardware MapHardware(IHardware hardware) => new()
    {
        Name = hardware.Name,
        Identifier = hardware.Identifier.ToString()
    };

    private SharedMemorySensor MapSensor(ISensor sensor) => new()
    {
        Name = sensor.Name,
        Identifier = sensor.Identifier.ToString(),
        SensorType = (int)sensor.SensorType,
        Value = float.IsNaN(sensor.Value ?? 0f) ? 0f : (sensor.Value ?? 0f),
        Sensor = sensor
    };
}