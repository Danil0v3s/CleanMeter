package app.cleanmeter.target.desktop.ui.settings.tabs.stats

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.KeyboardShortcutInfoLabel
import app.cleanmeter.target.desktop.ui.components.section.DropdownSection
import app.cleanmeter.target.desktop.ui.settings.CheckboxSectionOption
import app.cleanmeter.target.desktop.ui.settings.SectionType
import app.cleanmeter.target.desktop.ui.settings.SensorType
import java.awt.GraphicsEnvironment

internal fun List<CheckboxSectionOption>.filterOptions(vararg optionType: SensorType) =
    this.filter { source -> optionType.any { it == source.type } }

@Composable
fun StatsUi(
    overlaySettings: OverlaySettings,
    onOptionsToggle: (CheckboxSectionOption) -> Unit,
    onSectionSwitchToggle: (SectionType, Boolean) -> Unit,
    onCustomSensorSelect: (SensorType, String) -> Unit,
    onDisplaySelect: (Int) -> Unit,
    onFpsApplicationSelect: (String) -> Unit,
    getCpuSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    getGpuSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    getNetworkSensorReadings: () -> List<HardwareMonitorData.Sensor>,
    getHardwareSensors: () -> List<HardwareMonitorData.Hardware>,
    getPresentMonApps: () -> List<String>,
    onBoundaryChange: (SensorType, OverlaySettings.Sensor.GraphSensor.Boundaries) -> Unit,
    getSensor: (SensorType) -> OverlaySettings.Sensor,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val screenDevices = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices }
    val availableOptions = remember(overlaySettings) { checkboxSectionOptions(overlaySettings) }

    KeyboardShortcutInfoLabel()

    FpsStats(
        availableOptions = availableOptions,
        onSectionSwitchToggle = onSectionSwitchToggle,
        onOptionsToggle = onOptionsToggle,
        onFpsApplicationSelect = onFpsApplicationSelect,
        getPresentMonApps = getPresentMonApps,
    )

    GpuStats(
        availableOptions = availableOptions,
        onSectionSwitchToggle = onSectionSwitchToggle,
        onOptionsToggle = onOptionsToggle,
        getGpuSensorReadings = getGpuSensorReadings,
        onCustomSensorSelect = onCustomSensorSelect,
        onBoundaryChange = onBoundaryChange,
        getSensor = getSensor,
    )

    CpuStats(
        availableOptions = availableOptions,
        onSectionSwitchToggle = onSectionSwitchToggle,
        onOptionsToggle = onOptionsToggle,
        getCpuSensorReadings = getCpuSensorReadings,
        onCustomSensorSelect = onCustomSensorSelect,
        onBoundaryChange = onBoundaryChange,
        getSensor = getSensor,
    )

    RamStats(
        availableOptions = availableOptions,
        onSectionSwitchToggle = onSectionSwitchToggle,
        onOptionsToggle = onOptionsToggle,
    )

    NetworkStats(
        availableOptions = availableOptions,
        onSectionSwitchToggle = onSectionSwitchToggle,
        onOptionsToggle = onOptionsToggle,
        onCustomSensorSelect = onCustomSensorSelect,
        getNetworkSensorReadings = getNetworkSensorReadings,
        getHardwareSensors = getHardwareSensors,
    )

    DropdownSection(
        title = "MONITOR",
        options = screenDevices.map { it.defaultConfiguration.device.iDstring },
        selectedIndex = overlaySettings.selectedDisplayIndex,
        onValueChanged = { onDisplaySelect(it) }
    )

    Text(
        text = "May your frames be high, and temps be low.",
        color = LocalColorScheme.current.text.disabled,
        style = LocalTypography.current.labelSMedium,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun checkboxSectionOptions(overlaySettings: OverlaySettings) = listOf(
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.framerate.isEnabled,
        name = "Frame count",
        type = SensorType.Framerate,
        dataType = HardwareMonitorData.SensorType.SmallData,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.frametime.isEnabled,
        name = "Frame time graph",
        type = SensorType.Frametime,
        dataType = HardwareMonitorData.SensorType.Unknown,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.cpuTemp.isEnabled,
        name = "CPU temperature",
        type = SensorType.CpuTemp,
        optionReadingId = overlaySettings.sensors.cpuTemp.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Temperature,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.cpuUsage.isEnabled,
        name = "CPU usage",
        type = SensorType.CpuUsage,
        optionReadingId = overlaySettings.sensors.cpuUsage.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Load,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.gpuTemp.isEnabled,
        name = "GPU temperature",
        type = SensorType.GpuTemp,
        optionReadingId = overlaySettings.sensors.gpuTemp.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Temperature,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.gpuUsage.isEnabled,
        name = "GPU usage",
        type = SensorType.GpuUsage,
        optionReadingId = overlaySettings.sensors.gpuUsage.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Load,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.vramUsage.isEnabled,
        name = "VRAM usage",
        type = SensorType.VramUsage,
        optionReadingId = overlaySettings.sensors.vramUsage.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Load,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.vramUsage.isEnabled,
        name = "Total VRAM used",
        type = SensorType.TotalVramUsed,
        optionReadingId = overlaySettings.sensors.totalVramUsed.customReadingId,
        useCustomSensor = true,
        useCheckbox = false,
        dataType = HardwareMonitorData.SensorType.SmallData,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.ramUsage.isEnabled,
        name = "RAM usage",
        type = SensorType.RamUsage,
        dataType = HardwareMonitorData.SensorType.Load,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.downRate.isEnabled,
        name = "Receive speed",
        type = SensorType.DownRate,
        optionReadingId = overlaySettings.sensors.downRate.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Throughput,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.sensors.upRate.isEnabled,
        name = "Send speed",
        type = SensorType.UpRate,
        optionReadingId = overlaySettings.sensors.upRate.customReadingId,
        useCustomSensor = true,
        dataType = HardwareMonitorData.SensorType.Throughput,
    ),
    CheckboxSectionOption(
        isSelected = overlaySettings.netGraph,
        name = "Network graph",
        type = SensorType.NetGraph,
        dataType = HardwareMonitorData.SensorType.Unknown,
    ),
)