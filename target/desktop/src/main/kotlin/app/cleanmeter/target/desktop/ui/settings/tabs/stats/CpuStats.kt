package app.cleanmeter.target.desktop.ui.settings.tabs.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.CheckboxWithLabel
import app.cleanmeter.target.desktop.ui.components.SensorBoundaryInput
import app.cleanmeter.target.desktop.ui.components.dropdown.SensorReadingDropdownMenu
import app.cleanmeter.target.desktop.ui.components.section.CustomBodyCheckboxSection
import app.cleanmeter.target.desktop.ui.settings.CheckboxSectionOption
import app.cleanmeter.target.desktop.ui.settings.SectionType
import app.cleanmeter.target.desktop.ui.settings.SensorType

@Composable
internal fun CpuStats(
    availableOptions: List<CheckboxSectionOption>,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
    getCpuSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    onCustomSensorSelect: (SensorType, String) -> Unit,
    onBoundaryChange: (SensorType, OverlaySettings.Sensor.GraphSensor.Boundaries) -> Unit,
    getSensor: (SensorType) -> OverlaySettings.Sensor,
) {
    CustomBodyCheckboxSection(
        title = "CPU",
        options = availableOptions.filterOptions(SensorType.CpuUsage, SensorType.CpuTemp),
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Cpu, it) },
        body = { options ->
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    val readings = getCpuSensorReadings().filter { it.SensorType == option.dataType }

                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        CheckboxWithLabel(
                            label = option.name,
                            onCheckedChange = { onOptionsToggle(option.copy(isSelected = !option.isSelected)) },
                            checked = option.isSelected,
                        )
                        if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                            SensorReadingDropdownMenu(
                                options = readings,
                                onValueChanged = {
                                    onCustomSensorSelect(option.type, it.Identifier)
                                },
                                selectedIndex = readings
                                    .indexOfFirst { it.Identifier == option.optionReadingId }
                                    .coerceAtLeast(0),
                                label = "Sensor:",
                                sensorName = option.name,
                            )
                            SensorBoundaryInput(
                                sensor = getSensor(option.type),
                                sensorType = option.type,
                                onBoundaryChange = onBoundaryChange,
                            )
                        }
                    }
                }
            }
        }
    )
}