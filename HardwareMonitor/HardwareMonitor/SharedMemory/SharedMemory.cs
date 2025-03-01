using LibreHardwareMonitor.Hardware;

namespace HardwareMonitor.SharedMemory;

public static class SharedMemoryConsts
{
    public const string SharedMemoryName = "CleanMeterHardwareMonitor";

    // Long lastPollTime = 8 bytes
    // Hardware Count = 4 bytes
    // Sensor Count = 4 bytes

    // Hardware size = 260 bytes
    // |-- string name = 128 bytes
    // |-- string identifier = 128 bytes
    // |-- HardwareType type = 4 bytes

    // Sensor size = 392 bytes
    // |-- string name = 128 bytes
    // |-- string identifier = 128 bytes
    // |-- string hardwareIdentifier = 128 bytes
    // |-- SensorType type = 4 bytes
    // |-- float value = 4 bytes

    public const int IdentifierSize = 128;
    public const int NameSize = 128;
    public const int HardwareSize = 260;
    public const int SensorSize = 392;
}

public class SharedMemoryHardware
{
    public required IHardware Hardware;
    public required string Name { get; set; }
    public required string Identifier { get; set; }
    public required HardwareType HardwareType { get; set; }

    private bool _isActive = true;

    public void Update()
    {
        if (_isActive)
        {
            Hardware.Update();
        }
    }

    public void StopUpdates()
    {
        _isActive = false;
    }
}

public class SharedMemorySensor
{
    public required ISensor HardwareSensor;
    public required string Name { get; set; }
    public required string Identifier { get; set; }
    public required string HardwareIdentifier { get; set; }
    public required SensorType SensorType { get; set; }
    public required float Value { get; set; }
};

public class SharedMemoryData
{
    public long LastPollTime { get; set; }
    public List<SharedMemoryHardware> Hardwares { get; set; } = [];
    public List<SharedMemorySensor> Sensors { get; set; } = [];
}