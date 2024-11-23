using System.Text.Json.Serialization;
using LibreHardwareMonitor.Hardware;

namespace HardwareMonitor.SharedMemory;

public static class SharedMemoryConsts
{
    public const string SharedMemoryName = "CleanMeterHardwareMonitor";
}

public class SharedMemoryHardware
{
    public required IHardware Hardware;
    public required string Name { get; set; }
    public required string Identifier { get; set; }
    public required HardwareType HardwareType { get; set; }
    
    public bool ShouldSerializeHardware()
    {
        return false;
    }
}

public class SharedMemorySensor
{
    public required ISensor Sensor;
    public required string Name { get; set; }
    public required string Identifier { get; set; }
    public required SensorType SensorType { get; set; }
    public required float Value { get; set; }
    public required string HardwareIdentifier { get; set; }

    public bool ShouldSerializeSensor()
    {
        return false;
    }
};

public class SharedMemoryData
{
    public long LastPollTime { get; set; }
    public List<SharedMemoryHardware> Hardwares { get; set; } = [];
    public List<SharedMemorySensor> Sensors { get; set; } = [];
}

[JsonSourceGenerationOptions(WriteIndented = false, GenerationMode = JsonSourceGenerationMode.Serialization)]
[JsonSerializable(typeof(SharedMemoryData))]
[JsonSerializable(typeof(SharedMemorySensor))]
[JsonSerializable(typeof(SharedMemoryHardware))]
internal partial class SerializeContext : JsonSerializerContext
{
}