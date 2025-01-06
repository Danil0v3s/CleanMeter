#pragma warning disable CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider adding the 'required' modifier or declaring as nullable.
#pragma warning disable CS0067 // Never used
#pragma warning disable CS8625 // cannot convert null literal to non-nullable reference type
using LibreHardwareMonitor.Hardware;

namespace HardwareMonitor.PresentMon;

public class PresentMonHardware : IHardware
{
    public void Accept(IVisitor visitor)
    {
    }

    public void Traverse(IVisitor visitor)
    {
    }

    public string GetReport()
    {
        return "presentmon n/a";
    }

    public void Update()
    {
        throw new NotImplementedException();
    }

    public HardwareType HardwareType { get; } = HardwareType.EmbeddedController;
    public Identifier Identifier { get; } = new Identifier("presentmon");
    public string Name { get; set; } = "presentmon";
    public IHardware Parent { get; } = null;
    public ISensor[] Sensors { get; }
    public IHardware[] SubHardware { get; }
    public IDictionary<string, string> Properties { get; }
    public event SensorEventHandler? SensorAdded;
    public event SensorEventHandler? SensorRemoved;
}