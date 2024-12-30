namespace HardwareMonitor.Monitor;

public enum MonitorPacketCommand : short
{
    Data = 0,
    RefreshPresentMonApps = 1,
    SelectPresentMonApp = 2,
    PresentMonApps = 3,
    SelectPollingRate = 4
}