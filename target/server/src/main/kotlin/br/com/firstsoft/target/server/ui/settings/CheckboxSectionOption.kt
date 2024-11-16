package br.com.firstsoft.target.server.ui.settings

import br.com.firstsoft.target.server.ui.models.OverlaySettings

data class CheckboxSectionOption(
    val isSelected: Boolean,
    val name: String,
    val type: SettingsOptionType,
    val customOptionReading: OverlaySettings.CustomReading = OverlaySettings.CustomReading(),
    val useCustomSensor: Boolean = false,
    val useCheckbox: Boolean = true,
)

enum class SettingsOptionType {
    Framerate, Frametime, CpuTemp, CpuUsage, GpuTemp, GpuUsage, VramUsage, VramCommitted, RamUsage, UpRate, DownRate, NetGraph
}