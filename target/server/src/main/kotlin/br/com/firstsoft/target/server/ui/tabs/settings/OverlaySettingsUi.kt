package br.com.firstsoft.target.server.ui.tabs.settings

import PreferencesRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.BarelyVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray
import br.com.firstsoft.target.server.ui.components.CheckboxWithLabel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ui.app.OVERLAY_SETTINGS_PREFERENCE_KEY
import ui.app.OverlaySettings
import java.awt.GraphicsEnvironment

private fun List<CheckboxSectionOption>.filterOptions(vararg optionType: SettingsOptionType) =
    this.filter { source -> optionType.any { it == source.type } }

@Composable
fun OverlaySettingsUi(
    onOverlaySettings: (OverlaySettings) -> Unit,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val screenDevices = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices }
    var overlaySettings by remember { mutableStateOf(loadOverlaySettings()) }

    LaunchedEffect(overlaySettings) {
        PreferencesRepository.setPreference(OVERLAY_SETTINGS_PREFERENCE_KEY, Json.encodeToString(overlaySettings))
        onOverlaySettings(overlaySettings)
    }

    val availableOptions = remember(overlaySettings) {
        listOf(
            CheckboxSectionOption(
                isSelected = overlaySettings.fps,
                name = "Frame count",
                type = SettingsOptionType.Framerate
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.frametime,
                name = "Frame time graph",
                type = SettingsOptionType.Frametime
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.cpuTemp,
                name = "CPU temperature",
                type = SettingsOptionType.CpuTemp
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.cpuUsage,
                name = "CPU usage",
                type = SettingsOptionType.CpuUsage
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.gpuTemp,
                name = "GPU temperature",
                type = SettingsOptionType.GpuTemp
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.gpuUsage,
                name = "GPU usage",
                type = SettingsOptionType.GpuUsage
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.vramUsage,
                name = "Vram usage",
                type = SettingsOptionType.VramUsage
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.ramUsage,
                name = "Ram usage",
                type = SettingsOptionType.RamUsage
            ),
        )
    }

    fun onOptionsToggle(option: CheckboxSectionOption) {
        overlaySettings = when (option.type) {
            SettingsOptionType.Framerate -> overlaySettings.copy(fps = option.isSelected)
            SettingsOptionType.Frametime -> overlaySettings.copy(frametime = option.isSelected)
            SettingsOptionType.CpuTemp -> overlaySettings.copy(cpuTemp = option.isSelected)
            SettingsOptionType.CpuUsage -> overlaySettings.copy(cpuUsage = option.isSelected)
            SettingsOptionType.GpuTemp -> overlaySettings.copy(gpuTemp = option.isSelected)
            SettingsOptionType.GpuUsage -> overlaySettings.copy(gpuUsage = option.isSelected)
            SettingsOptionType.VramUsage -> overlaySettings.copy(vramUsage = option.isSelected)
            SettingsOptionType.RamUsage -> overlaySettings.copy(ramUsage = option.isSelected)
        }
    }

    CheckboxSection(title = "FPS",
        options = availableOptions.filterOptions(SettingsOptionType.Framerate, SettingsOptionType.Frametime),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = {
            overlaySettings = overlaySettings.copy(fps = it, frametime = it)
        }
    )

    CheckboxSection(title = "GPU",
        options = availableOptions.filterOptions(
            SettingsOptionType.GpuUsage,
            SettingsOptionType.GpuTemp,
            SettingsOptionType.VramUsage
        ),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = {
            overlaySettings = overlaySettings.copy(gpuTemp = it, gpuUsage = it, vramUsage = it)
        }
    )

    CheckboxSection(title = "CPU",
        options = availableOptions.filterOptions(SettingsOptionType.CpuUsage, SettingsOptionType.CpuTemp),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = {
            overlaySettings = overlaySettings.copy(cpuTemp = it, cpuUsage = it)
        }
    )

    CheckboxSection(title = "RAM",
        options = availableOptions.filterOptions(SettingsOptionType.RamUsage),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = {
            overlaySettings = overlaySettings.copy(ramUsage = it)
        }
    )

    DropdownSection(title = "MONITOR",
        options = screenDevices.map { it.defaultConfiguration.device.iDstring },
        selectedIndex = overlaySettings.selectedDisplayIndex,
        onValueChanged = { overlaySettings = overlaySettings.copy(selectedDisplayIndex = it) }
    )
}

private fun loadOverlaySettings(): OverlaySettings {
    val json = PreferencesRepository.getPreferenceString(OVERLAY_SETTINGS_PREFERENCE_KEY)
    val settings = if (json != null) {
        try {
            Json.decodeFromString<OverlaySettings>(json)
        } catch (e: Exception) {
            OverlaySettings()
        }
    } else {
        OverlaySettings()
    }
    return settings
}

data class CheckboxSectionOption(
    val isSelected: Boolean, val name: String, val type: SettingsOptionType
)

enum class SettingsOptionType {
    Framerate, Frametime, CpuTemp, CpuUsage, GpuTemp, GpuUsage, VramUsage, RamUsage,
}

@Composable
private fun CheckboxSection(
    title: String,
    options: List<CheckboxSectionOption>,
    onOptionToggle: (CheckboxSectionOption) -> Unit,
    onSwitchToggle: (Boolean) -> Unit,
) = Column(
    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = MutedGray,
            lineHeight = 0.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Switch(
            checked = options.any { it.isSelected }, onCheckedChange = onSwitchToggle
        )
    }

    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            CheckboxWithLabel(
                label = option.name,
                onCheckedChange = { onOptionToggle(option.copy(isSelected = !option.isSelected)) },
                checked = option.isSelected,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DropdownSection(
    title: String,
    options: List<String>,
    onValueChanged: (Int) -> Unit,
    selectedIndex: Int,
) = Column(
    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[selectedIndex]) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = MutedGray,
            lineHeight = 0.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }

    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { }, modifier = Modifier.clearAndSetSemantics { }) {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            "Trailing icon for exposed dropdown menu",
                            Modifier.rotate(
                                if (expanded)
                                    270f
                                else
                                    90f
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().border(1.dp, AlmostVisibleGray, RoundedCornerShape(16.dp)),
                textStyle = TextStyle(
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = RoundedCornerShape(16.dp),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    trailingIconColor = MutedGray,
                    focusedTrailingIconColor = MutedGray,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                options.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            selectedOption = item
                            onValueChanged(index)
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = item)
                    }
                }
            }
        }
    }
}