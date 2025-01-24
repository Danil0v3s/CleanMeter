package app.cleanmeter.target.desktop.ui.settings.tabs.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.target.desktop.ui.components.CheckboxWithLabel
import app.cleanmeter.target.desktop.ui.components.dropdown.SensorReadingDropdownMenu
import app.cleanmeter.target.desktop.ui.components.section.CustomBodyCheckboxSection
import app.cleanmeter.target.desktop.ui.settings.CheckboxSectionOption
import app.cleanmeter.target.desktop.ui.settings.SectionType
import app.cleanmeter.target.desktop.ui.settings.SensorType

@Composable
internal fun NetworkStats(
    availableOptions: List<CheckboxSectionOption>,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
    getNetworkSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    getHardwareSensors: () -> List<HardwareMonitorData.Hardware>,
    onCustomSensorSelect: (SensorType, String) -> Unit,
) {
    CustomBodyCheckboxSection(
        title = "NETWORK",
        options = availableOptions.filterOptions(
            SensorType.DownRate,
            SensorType.UpRate,
            SensorType.NetGraph,
        ),
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Network, it) },
        body = { options ->
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    val readings = getNetworkSensorReadings().sortedBy { it.HardwareIdentifier }.filter { it.SensorType == option.dataType }.takeIf { it.isNotEmpty() } ?: getNetworkSensorReadings()

                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        CheckboxWithLabel(
                            label = option.name,
                            enabled = option.useCheckbox,
                            onCheckedChange = { onOptionsToggle(option.copy(isSelected = !option.isSelected)) },
                            checked = option.isSelected,
                        )

                        if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                            SensorReadingDropdownMenu(
                                dropdownLabel = {
                                    "${getHardwareSensors().firstOrNull { hardware -> hardware.Identifier == it.HardwareIdentifier }?.Name}: ${it.Name} (${it.Value} - ${it.SensorType})"
                                },
                                options = readings,
                                onValueChanged = {
                                    onCustomSensorSelect(option.type, it.Identifier)
                                },
                                selectedIndex = readings.indexOfFirst { it.Identifier == option.optionReadingId },
                                label = "Sensor:",
                                sensorName = option.name,
                            )
                        }
                    }
                }
            }
        }
    )
}