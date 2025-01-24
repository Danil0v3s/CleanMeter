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
internal fun GpuStats(
    availableOptions: List<CheckboxSectionOption>,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
    getGpuSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    onCustomSensorSelect: (SensorType, String) -> Unit,
    onBoundaryChange: (SensorType, OverlaySettings.Sensor.GraphSensor.Boundaries) -> Unit,
    getSensor: (SensorType) -> OverlaySettings.Sensor,
) {
    CustomBodyCheckboxSection(
        title = "GPU",
        options = availableOptions.filterOptions(
            SensorType.GpuUsage,
            SensorType.GpuTemp,
            SensorType.VramUsage,
            SensorType.TotalVramUsed,
        ),
        onSwitchToggle = { onSectionSwitchToggle(SectionType.Gpu, it) },
        body = { options ->
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    val readings = getGpuSensorReadings().filter { it.SensorType == option.dataType }.takeIf { it.isNotEmpty() } ?: getGpuSensorReadings()

                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        CheckboxWithLabel(
                            label = option.name,
                            enabled = option.useCheckbox,
                            onCheckedChange = { onOptionsToggle(option.copy(isSelected = !option.isSelected)) },
                            checked = option.isSelected,
                        )

                        if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                            SensorReadingDropdownMenu(
                                options = readings,
                                onValueChanged = { onCustomSensorSelect(option.type, it.Identifier) },
                                selectedIndex = readings.indexOfFirst { it.Identifier == option.optionReadingId },
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