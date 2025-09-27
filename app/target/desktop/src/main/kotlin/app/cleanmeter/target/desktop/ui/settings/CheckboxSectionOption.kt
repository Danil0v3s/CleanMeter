package app.cleanmeter.target.desktop.ui.settings

import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData

data class CheckboxSectionOption(
    val isSelected: Boolean,
    val name: String,
    val type: SensorType,
    val dataType: HardwareMonitorData.SensorType,
    val optionReadingId: String = "",
    val useCustomSensor: Boolean = false,
    val useCheckbox: Boolean = true,
)

enum class SensorType {
    Framerate, Frametime, CpuTemp, CpuUsage, CpuConsumption, GpuTemp, GpuUsage, VramUsage, TotalVramUsed, GpuConsumption, RamUsage, UpRate, DownRate, NetGraph
}

enum class SectionType {
    Fps, Gpu, Cpu, Ram, Network,
}