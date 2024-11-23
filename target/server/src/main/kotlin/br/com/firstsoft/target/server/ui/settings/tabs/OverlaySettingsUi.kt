package br.com.firstsoft.target.server.ui.settings.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hwinfo.SensorReadingElement
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.components.CheckboxSection
import br.com.firstsoft.target.server.ui.components.CheckboxWithLabel
import br.com.firstsoft.target.server.ui.components.CustomBodyCheckboxSection
import br.com.firstsoft.target.server.ui.components.DropdownSection
import br.com.firstsoft.target.server.ui.components.KeyboardShortcutInfoLabel
import br.com.firstsoft.target.server.ui.components.StealthDropdownMenu
import br.com.firstsoft.target.server.ui.settings.CheckboxSectionOption
import br.com.firstsoft.target.server.ui.settings.SectionType
import br.com.firstsoft.target.server.ui.settings.SensorType
import java.awt.GraphicsEnvironment

private fun List<CheckboxSectionOption>.filterOptions(vararg optionType: SensorType) =
    this.filter { source -> optionType.any { it == source.type } }

@Composable
fun OverlaySettingsUi(
    overlaySettings: OverlaySettings,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onCustomSensorSelect: (SensorType, String) -> Unit,
    onDisplaySelect: (Int) -> Unit,
    getCpuSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    getGpuSensorReadings: () -> List<HardwareMonitorData.Sensor>,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val screenDevices = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices }
    val availableOptions = remember(overlaySettings) { checkboxSectionOptions(overlaySettings) }

    KeyboardShortcutInfoLabel()

    CheckboxSection(
        title = "FPS",
        options = availableOptions.filterOptions(SensorType.Framerate, SensorType.Frametime),
        onOptionToggle = onOptionsToggle,
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Fps, it) }
    )

    CustomBodyCheckboxSection(
        title = "GPU",
        options = availableOptions.filterOptions(
            SensorType.GpuUsage,
            SensorType.GpuTemp,
            SensorType.VramUsage
        ),
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Gpu, it) },
        body = {
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val readings = getGpuSensorReadings()
                availableOptions.filterOptions(
                    SensorType.GpuUsage,
                    SensorType.GpuTemp,
                    SensorType.VramUsage
                )
                    .forEach { option ->
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                            CheckboxWithLabel(
                                label = option.name,
                                onCheckedChange = { onOptionsToggle(option.copy(isSelected = !option.isSelected)) },
                                checked = option.isSelected,
                            )
                            if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                                StealthDropdownMenu(
                                    modifier = Modifier.padding(start = 18.dp),
                                    options = readings.map { "${it.Name} (${it.Value} - ${HardwareMonitorData.SensorType.fromValue(it.SensorType)})" },
                                    onValueChanged = {
                                        val reading = readings[it].Identifier
                                        onCustomSensorSelect(option.type, reading)
                                    },
                                    selectedIndex = readings
                                        .indexOfFirst { it.Identifier == option.optionReadingId }
                                        .coerceAtLeast(0),
                                    label = "Sensor:"
                                )
                            }
                        }
                    }
            }
        }
    )

    CustomBodyCheckboxSection(
        title = "CPU",
        options = availableOptions.filterOptions(SensorType.CpuUsage, SensorType.CpuTemp),
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Cpu, it) },
        body = {
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val readings = getCpuSensorReadings()
                availableOptions.filterOptions(SensorType.CpuUsage, SensorType.CpuTemp)
                    .forEach { option ->
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                            CheckboxWithLabel(
                                label = option.name,
                                onCheckedChange = { onOptionsToggle(option.copy(isSelected = !option.isSelected)) },
                                checked = option.isSelected,
                            )
                            if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                                StealthDropdownMenu(
                                    modifier = Modifier.padding(start = 18.dp),
                                    options = readings.map { "${it.Name} (${it.Value} - ${HardwareMonitorData.SensorType.fromValue(it.SensorType)})" },
                                    onValueChanged = {
                                        val reading = readings[it].Identifier
                                        onCustomSensorSelect(option.type, reading)
                                    },
                                    selectedIndex = readings
                                        .indexOfFirst { it.Identifier == option.optionReadingId }
                                        .coerceAtLeast(0),
                                    label = "Sensor:"
                                )
                            }
                        }
                    }
            }
        }
    )

    CheckboxSection(
        title = "RAM",
        options = availableOptions.filterOptions(SensorType.RamUsage),
        onOptionToggle = onOptionsToggle,
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Ram, it) }
    )

    CheckboxSection(
        title = "NETWORK",
        options = availableOptions.filterOptions(
            SensorType.DownRate,
            SensorType.UpRate,
            SensorType.NetGraph
        ),
        onOptionToggle = onOptionsToggle,
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Network, it) }
    )

    DropdownSection(
        title = "MONITOR",
        options = screenDevices.map { it.defaultConfiguration.device.iDstring },
        selectedIndex = overlaySettings.selectedDisplayIndex,
        onValueChanged = { onDisplaySelect(it) }
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

private fun checkboxSectionOptions(overlaySettings: OverlaySettings) = listOf(
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.framerate.isEnabled,
        name = "Frame count",
        type = SensorType.Framerate
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.frametime.isEnabled,
        name = "Frame time graph",
        type = SensorType.Frametime
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.cpuTemp.isEnabled,
        name = "CPU temperature",
        type = SensorType.CpuTemp,
        optionReadingId = overlaySettings.sensors.cpuTemp.customReadingId,
        useCustomSensor = true
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.cpuUsage.isEnabled,
        name = "CPU usage",
        type = SensorType.CpuUsage,
        optionReadingId = overlaySettings.sensors.cpuUsage.customReadingId,
        useCustomSensor = true
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.gpuTemp.isEnabled,
        name = "GPU temperature",
        type = SensorType.GpuTemp,
        optionReadingId = overlaySettings.sensors.gpuTemp.customReadingId,
        useCustomSensor = true
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.gpuUsage.isEnabled,
        name = "GPU usage",
        type = SensorType.GpuUsage,
        optionReadingId = overlaySettings.sensors.gpuUsage.customReadingId,
        useCustomSensor = true
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.vramUsage.isEnabled,
        name = "VRAM usage",
        type = SensorType.VramUsage
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.ramUsage.isEnabled,
        name = "RAM usage",
        type = SensorType.RamUsage
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.downRate.isEnabled,
        name = "Receive speed",
        type = SensorType.DownRate
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.upRate.isEnabled,
        name = "Send speed",
        type = SensorType.UpRate
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.netGraph,
        name = "Network graph",
        type = SensorType.NetGraph
    ),
)