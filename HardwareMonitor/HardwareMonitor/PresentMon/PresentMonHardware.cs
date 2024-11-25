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