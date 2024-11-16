package br.com.firstsoft.target.server.ui.settings.tabs.overlaysettings.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.firstsoft.core.common.hwinfo.SensorReadingElement
import br.com.firstsoft.target.server.ui.components.CheckboxWithLabel
import br.com.firstsoft.target.server.ui.components.CustomBodyCheckboxSection
import br.com.firstsoft.target.server.ui.components.SensorReadingDropdownMenu
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.settings.CheckboxSectionOption
import br.com.firstsoft.target.server.ui.settings.SettingsOptionType

@Composable
internal fun gpu(
    availableOptions: List<CheckboxSectionOption>,
    onOverlaySettings: (OverlaySettings) -> Unit,
    overlaySettings: OverlaySettings,
    getGpuSensorReadings: () -> List<SensorReadingElement>,
    onCheckedChange: (CheckboxSectionOption) -> Unit,
) {
    CustomBodyCheckboxSection(
        title = "GPU",
        options = availableOptions.filterOptions(
            SettingsOptionType.GpuUsage,
            SettingsOptionType.GpuTemp,
            SettingsOptionType.VramUsage,
            SettingsOptionType.VramCommitted,
        ),
        onSwitchToggle = { onOverlaySettings(overlaySettings.copy(gpuTemp = it, gpuUsage = it, vramUsage = it)) },
        body = { options ->
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val readings = getGpuSensorReadings()
                options.forEach { option ->
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        CheckboxWithLabel(
                            enabled = option.useCheckbox,
                            label = option.name,
                            onCheckedChange = { onCheckedChange(option.copy(isSelected = !option.isSelected)) },
                            checked = option.isSelected,
                        )
                        if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                            SensorReadingDropdownMenu(
                                modifier = Modifier.padding(start = 18.dp),
                                options = readings,
                                onValueChanged = {
                                    val customReading = OverlaySettings.CustomReading(
                                        it.dwReadingID,
                                        it.dwSensorIndex
                                    )
                                    when (option.type) {
                                        SettingsOptionType.GpuTemp -> onOverlaySettings(
                                            overlaySettings.copy(
                                                gpuTempReadingId = customReading
                                            )
                                        )

                                        SettingsOptionType.GpuUsage -> onOverlaySettings(
                                            overlaySettings.copy(
                                                gpuUsageReadingId = customReading
                                            )
                                        )

                                        SettingsOptionType.VramUsage -> onOverlaySettings(
                                            overlaySettings.copy(
                                                vramUsageReadingId = customReading
                                            )
                                        )

                                        SettingsOptionType.VramCommitted -> onOverlaySettings(
                                            overlaySettings.copy(
                                                vramCommittedReadingId = customReading
                                            )
                                        )

                                        else -> Unit
                                    }
                                },
                                selectedIndex = readings
                                    .indexOfFirst { it.dwReadingID == option.customOptionReading.readingId && it.dwSensorIndex == option.customOptionReading.sensorIndex }
                                    .coerceAtLeast(0),
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