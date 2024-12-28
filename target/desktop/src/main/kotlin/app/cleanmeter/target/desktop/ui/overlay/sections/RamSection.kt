package app.cleanmeter.target.desktop.ui.overlay.sections

import androidx.compose.runtime.Composable
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.common.hardwaremonitor.RamUsage
import app.cleanmeter.core.common.hardwaremonitor.RamUsagePercent
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.Pill
import app.cleanmeter.target.desktop.ui.components.Progress
import java.util.*

@Composable
internal fun RamSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.ramUsage.isEnabled) {
        Pill(
            title = "RAM",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            Progress(
                value = data.RamUsagePercent,
                label = String.format("%02.1f", data.RamUsage, Locale.US),
                unit = "GB",
                progressType = overlaySettings.progressType,
                boundaries = overlaySettings.sensors.ramUsage.boundaries,
            )
        }
    }
}