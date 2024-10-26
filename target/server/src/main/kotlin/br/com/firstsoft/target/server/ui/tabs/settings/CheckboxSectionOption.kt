package br.com.firstsoft.target.server.ui.tabs.settings

data class CheckboxSectionOption(
    val isSelected: Boolean, val name: String, val type: SettingsOptionType
)

enum class SettingsOptionType {
    Framerate, Frametime, CpuTemp, CpuUsage, GpuTemp, GpuUsage, VramUsage, RamUsage, UpRate, DownRate, NetGraph
}