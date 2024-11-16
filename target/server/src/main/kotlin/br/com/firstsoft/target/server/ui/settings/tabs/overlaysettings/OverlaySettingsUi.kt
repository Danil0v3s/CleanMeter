package br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.core.common.hwinfo.SensorReadingElement
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.components.CheckboxSection
import br.com.firstsoft.target.server.ui.components.DropdownSection
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.settings.CheckboxSectionOption
import br.com.firstsoft.target.server.ui.settings.SettingsOptionType
import br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections.cpu
import br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections.filterOptions
import br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections.fps
import br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections.gpu
import br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections.shortcutTooltip
import java.awt.GraphicsEnvironment

@Composable
fun OverlaySettingsUi(
    overlaySettings: OverlaySettings,
    onOverlaySettings: (OverlaySettings) -> Unit,
    getCpuSensorReadings: () -> List<SensorReadingElement>,
    getGpuSensorReadings: () -> List<SensorReadingElement>,
    getPresentMonReadings: () -> List<SensorReadingElement>,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val screenDevices = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices }
    val availableOptions = remember(overlaySettings) { createOptions(overlaySettings) }

    fun onOptionsToggle(option: CheckboxSectionOption) {
        val newSettings = when (option.type) {
            SettingsOptionType.Framerate -> overlaySettings.copy(fps = option.isSelected)
            SettingsOptionType.Frametime -> overlaySettings.copy(frametime = option.isSelected)
            SettingsOptionType.CpuTemp -> overlaySettings.copy(cpuTemp = option.isSelected)
            SettingsOptionType.CpuUsage -> overlaySettings.copy(cpuUsage = option.isSelected)
            SettingsOptionType.GpuTemp -> overlaySettings.copy(gpuTemp = option.isSelected)
            SettingsOptionType.GpuUsage -> overlaySettings.copy(gpuUsage = option.isSelected)
            SettingsOptionType.VramUsage -> overlaySettings.copy(vramUsage = option.isSelected)
            SettingsOptionType.VramCommitted -> overlaySettings.copy(vramUsage = option.isSelected) // same as vram usage
            SettingsOptionType.RamUsage -> overlaySettings.copy(ramUsage = option.isSelected)
            SettingsOptionType.UpRate -> overlaySettings.copy(upRate = option.isSelected)
            SettingsOptionType.DownRate -> overlaySettings.copy(downRate = option.isSelected)
            SettingsOptionType.NetGraph -> overlaySettings.copy(netGraph = option.isSelected)
        }
        onOverlaySettings(newSettings)
    }

    shortcutTooltip()
    fps(availableOptions, onOverlaySettings, overlaySettings, getPresentMonReadings, ::onOptionsToggle)
    gpu(availableOptions, onOverlaySettings, overlaySettings, getGpuSensorReadings, ::onOptionsToggle)
    cpu(availableOptions, onOverlaySettings, overlaySettings, getCpuSensorReadings, ::onOptionsToggle)

    CheckboxSection(title = "RAM",
        options = availableOptions.filterOptions(SettingsOptionType.RamUsage),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = { onOverlaySettings(overlaySettings.copy(ramUsage = it)) }
    )

    CheckboxSection(title = "NETWORK",
        options = availableOptions.filterOptions(
            SettingsOptionType.DownRate,
            SettingsOptionType.UpRate,
            SettingsOptionType.NetGraph
        ),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = { onOverlaySettings(overlaySettings.copy(downRate = it, upRate = it, netGraph = it)) }
    )

    DropdownSection(title = "MONITOR",
        options = screenDevices.map { it.defaultConfiguration.device.iDstring },
        selectedIndex = overlaySettings.selectedDisplayIndex,
        onValueChanged = { onOverlaySettings(overlaySettings.copy(selectedDisplayIndex = it)) }
    )

    Text(
        text = "May your frames be high, and temps be low.",
        fontSize = 12.sp,
        color = LabelGray,
        lineHeight = 0.sp,
        fontWeight = FontWeight(550),
        letterSpacing = 0.14.sp,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun createOptions(overlaySettings: OverlaySettings) = listOf(
    CheckboxSectionOption(
        isSelected = overlaySettings.fps,
        name = "Frame count",
        type = SettingsOptionType.Framerate,
        customOptionReading = overlaySettings.framerateReadingId,
        useCustomSensor = true,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.frametime,
        name = "Frame time graph",
        type = SettingsOptionType.Frametime
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.cpuTemp,
        name = "CPU temperature",
        type = SettingsOptionType.CpuTemp,
        customOptionReading = overlaySettings.cpuTempReadingId,
        useCustomSensor = true,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.cpuUsage,
        name = "CPU usage",
        type = SettingsOptionType.CpuUsage,
        customOptionReading = overlaySettings.cpuUsageReadingId,
        useCustomSensor = true,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.gpuTemp,
        name = "GPU temperature",
        type = SettingsOptionType.GpuTemp,
        customOptionReading = overlaySettings.gpuTempReadingId,
        useCustomSensor = true,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.gpuUsage,
        name = "GPU usage",
        type = SettingsOptionType.GpuUsage,
        customOptionReading = overlaySettings.gpuUsageReadingId,
        useCustomSensor = true,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.vramUsage,
        name = "VRAM usage (%)",
        type = SettingsOptionType.VramUsage,
        customOptionReading = overlaySettings.vramUsageReadingId,
        useCustomSensor = true,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.vramUsage,
        name = "VRAM committed",
        type = SettingsOptionType.VramCommitted,
        customOptionReading = overlaySettings.vramCommittedReadingId,
        useCustomSensor = true,
        useCheckbox = false,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.ramUsage,
        name = "RAM usage",
        type = SettingsOptionType.RamUsage
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.downRate,
        name = "Receive speed",
        type = SettingsOptionType.DownRate
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.upRate,
        name = "Send speed",
        type = SettingsOptionType.UpRate
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.netGraph,
        name = "Network graph",
        type = SettingsOptionType.NetGraph
    ),
)