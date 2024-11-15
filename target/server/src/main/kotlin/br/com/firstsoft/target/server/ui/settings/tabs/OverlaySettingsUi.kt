package br.com.firstsoft.target.server.ui.settings.tabs

import androidx.compose.foundation.Image
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.BorderGray
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.components.CheckboxSection
import br.com.firstsoft.target.server.ui.components.CheckboxWithLabel
import br.com.firstsoft.target.server.ui.components.CustomBodyCheckboxSection
import br.com.firstsoft.target.server.ui.components.DropdownSection
import br.com.firstsoft.target.server.ui.components.StealthDropdownMenu
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.settings.CheckboxSectionOption
import br.com.firstsoft.target.server.ui.settings.SettingsOptionType
import br.com.firstsoft.core.common.hwinfo.SensorReadingElement
import java.awt.GraphicsEnvironment

private fun List<CheckboxSectionOption>.filterOptions(vararg optionType: SettingsOptionType) =
    this.filter { source -> optionType.any { it == source.type } }

@Composable
fun OverlaySettingsUi(
    overlaySettings: OverlaySettings,
    onOverlaySettings: (OverlaySettings) -> Unit,
    getCpuSensorReadings: () -> List<SensorReadingElement>,
    getGpuSensorReadings: () -> List<SensorReadingElement>,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val screenDevices = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices }

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
                type = SettingsOptionType.CpuTemp,
                optionReadingId = overlaySettings.cpuTempReadingId,
                useCustomSensor = true
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.cpuUsage,
                name = "CPU usage",
                type = SettingsOptionType.CpuUsage,
                optionReadingId = overlaySettings.cpuUsageReadingId,
                useCustomSensor = true
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.gpuTemp,
                name = "GPU temperature",
                type = SettingsOptionType.GpuTemp,
                optionReadingId = overlaySettings.gpuTempReadingId,
                useCustomSensor = true
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.gpuUsage,
                name = "GPU usage",
                type = SettingsOptionType.GpuUsage,
                optionReadingId = overlaySettings.gpuUsageReadingId,
                useCustomSensor = true
            ),
            CheckboxSectionOption(
                isSelected = overlaySettings.vramUsage,
                name = "VRAM usage",
                type = SettingsOptionType.VramUsage
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
    }

    fun onOptionsToggle(option: CheckboxSectionOption) {
        val newSettings = when (option.type) {
            SettingsOptionType.Framerate -> overlaySettings.copy(fps = option.isSelected)
            SettingsOptionType.Frametime -> overlaySettings.copy(frametime = option.isSelected)
            SettingsOptionType.CpuTemp -> overlaySettings.copy(cpuTemp = option.isSelected)
            SettingsOptionType.CpuUsage -> overlaySettings.copy(cpuUsage = option.isSelected)
            SettingsOptionType.GpuTemp -> overlaySettings.copy(gpuTemp = option.isSelected)
            SettingsOptionType.GpuUsage -> overlaySettings.copy(gpuUsage = option.isSelected)
            SettingsOptionType.VramUsage -> overlaySettings.copy(vramUsage = option.isSelected)
            SettingsOptionType.RamUsage -> overlaySettings.copy(ramUsage = option.isSelected)
            SettingsOptionType.UpRate -> overlaySettings.copy(upRate = option.isSelected)
            SettingsOptionType.DownRate -> overlaySettings.copy(downRate = option.isSelected)
            SettingsOptionType.NetGraph -> overlaySettings.copy(netGraph = option.isSelected)
        }
        onOverlaySettings(newSettings)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(painterResource("icons/info.svg"), "")
            Text(
                text = "Hot key for showing/hiding the overlay",
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(bottom = 2.5.dp),
            )
        }
        Image(painterResource("icons/hotkey.png"), "")
    }

    CheckboxSection(title = "FPS",
        options = availableOptions.filterOptions(SettingsOptionType.Framerate, SettingsOptionType.Frametime),
        onOptionToggle = ::onOptionsToggle,
        onSwitchToggle = {
            onOverlaySettings(overlaySettings.copy(fps = it, frametime = it))
        }
    )

    CustomBodyCheckboxSection(
        title = "GPU",
        options = availableOptions.filterOptions(
            SettingsOptionType.GpuUsage,
            SettingsOptionType.GpuTemp,
            SettingsOptionType.VramUsage
        ),
        onSwitchToggle = { onOverlaySettings(overlaySettings.copy(gpuTemp = it, gpuUsage = it, vramUsage = it)) },
        body = {
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val readings = getGpuSensorReadings()
                availableOptions.filterOptions(
                    SettingsOptionType.GpuUsage,
                    SettingsOptionType.GpuTemp,
                    SettingsOptionType.VramUsage
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
                                    options = readings.map { "${it.szLabelOrig} (${it.value}${it.szUnit})" },
                                    onValueChanged = {
                                        val reading = readings[it].dwReadingID
                                        when (option.type) {
                                            SettingsOptionType.GpuTemp -> onOverlaySettings(
                                                overlaySettings.copy(
                                                    gpuTempReadingId = reading
                                                )
                                            )

                                            SettingsOptionType.GpuUsage -> onOverlaySettings(
                                                overlaySettings.copy(
                                                    gpuUsageReadingId = reading
                                                )
                                            )

                                            else -> Unit
                                        }
                                    },
                                    selectedIndex = readings
                                        .indexOfFirst { it.dwReadingID == option.optionReadingId }
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
        options = availableOptions.filterOptions(SettingsOptionType.CpuUsage, SettingsOptionType.CpuTemp),
        onSwitchToggle = { onOverlaySettings(overlaySettings.copy(cpuTemp = it, cpuUsage = it)) },
        body = {
            Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val readings = getCpuSensorReadings()
                availableOptions.filterOptions(SettingsOptionType.CpuUsage, SettingsOptionType.CpuTemp)
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
                                    options = readings.map { "${it.szLabelOrig} (${it.value}${it.szUnit})" },
                                    onValueChanged = {
                                        val reading = readings[it].dwReadingID
                                        when (option.type) {
                                            SettingsOptionType.CpuTemp -> onOverlaySettings(
                                                overlaySettings.copy(
                                                    cpuTempReadingId = reading
                                                )
                                            )

                                            SettingsOptionType.CpuUsage -> onOverlaySettings(
                                                overlaySettings.copy(
                                                    cpuUsageReadingId = reading
                                                )
                                            )

                                            else -> Unit
                                        }
                                    },
                                    selectedIndex = readings
                                        .indexOfFirst { it.dwReadingID == option.optionReadingId }
                                        .coerceAtLeast(0),
                                    label = "Sensor:"
                                )
                            }
                        }
                    }
            }
        }
    )

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