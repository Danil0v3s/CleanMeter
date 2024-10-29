package br.com.firstsoft.target.server.ui.settings

data class CheckboxSectionOption(
    val isSelected: Boolean,
    val name: String,
    val type: SettingsOptionType,
    val optionReadingId: Int = -1,
    val useCustomSensor: Boolean = false,
)

enum class SettingsOptionType {
    Framerate, Frametime, CpuTemp, CpuUsage, GpuTemp, GpuUsage, VramUsage, RamUsage, UpRate, DownRate, NetGraph
}