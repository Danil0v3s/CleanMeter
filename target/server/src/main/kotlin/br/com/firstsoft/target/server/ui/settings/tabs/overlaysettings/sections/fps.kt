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
import br.com.firstsoft.target.server.ui.components.StealthDropdownMenu
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.settings.CheckboxSectionOption
import br.com.firstsoft.target.server.ui.settings.SettingsOptionType

@Composable
internal fun fps(
    availableOptions: List<CheckboxSectionOption>,
    onOverlaySettings: (OverlaySettings) -> Unit,
    overlaySettings: OverlaySettings,
    getPresentMonReadings: () -> List<SensorReadingElement>,
    onCheckedChange: (CheckboxSectionOption) -> Unit,
) {
    CustomBodyCheckboxSection(
        title = "FPS",
        options = availableOptions.filterOptions(SettingsOptionType.Framerate, SettingsOptionType.Frametime),
        onSwitchToggle = {
            onOverlaySettings(overlaySettings.copy(fps = it, frametime = it))
        },
        body = { options ->
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val readings = getPresentMonReadings()
                options.forEach { option ->
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        CheckboxWithLabel(
                            label = option.name,
                            onCheckedChange = { onCheckedChange(option.copy(isSelected = !option.isSelected)) },
                            checked = option.isSelected,
                        )
                        if (readings.isNotEmpty() && option.isSelected && option.useCustomSensor) {
                            StealthDropdownMenu(
                                modifier = Modifier.padding(start = 18.dp),
                                options = readings.map { "${it.szLabelOrig} (${it.value}${it.szUnit})" },
                                onValueChanged = {
                                    val reading = readings[it]
                                    val customReading = OverlaySettings.CustomReading(
                                        reading.dwReadingID,
                                        reading.dwSensorIndex
                                    )

                                    when (option.type) {
                                        SettingsOptionType.Framerate -> onOverlaySettings(
                                            overlaySettings.copy(
                                                framerateReadingId = customReading
                                            )
                                        )

                                        else -> Unit
                                    }
                                },
                                selectedIndex = readings
                                    .indexOfFirst { it.dwReadingID == option.customOptionReading.readingId && it.dwSensorIndex == option.customOptionReading.sensorIndex }
                                    .coerceAtLeast(0),
                                label = "Sensor:"
                            )
                        }
                    }
                }
            }
        }
    )
}