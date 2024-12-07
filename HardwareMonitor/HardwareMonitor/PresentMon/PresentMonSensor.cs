using LibreHardwareMonitor.Hardware;

namespace HardwareMonitor.PresentMon;

public class PresentMonSensor(IHardware hardware, string identifier, int index, string name) : ISensor
{
    public void Accept(IVisitor visitor)
    {
    }

    public void Traverse(IVisitor visitor)
    {
    }

    public void ResetMin()
    {
    }

    public void ResetMax()
    {
    }

    public void ClearValues()
    {
    }

    public IControl Control { get; }
    public IHardware Hardware { get; } = hardware;
    public Identifier Identifier { get; } = new(hardware.Identifier, identifier);
    public int Index { get; } = index;
    public bool IsDefaultHidden { get; }
    public float? Max { get; }
    public float? Min { get; }
    public string Name { get; set; } = name;
    public IReadOnlyList<IParameter> Parameters { get; }
    public SensorType SensorType { get; } = SensorType.SmallData;
    public float? Value { get; set; }
    public IEnumerable<SensorValue> Values { get; }
    public TimeSpan ValuesTimeWindow { get; set; }
}