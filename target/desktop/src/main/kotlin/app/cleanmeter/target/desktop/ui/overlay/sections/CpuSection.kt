package app.cleanmeter.target.desktop.ui.overlay.sections

import androidx.compose.runtime.Composable
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.CustomReadingProgress
import app.cleanmeter.target.desktop.ui.components.Pill
import java.util.*

@Composable
internal fun CpuSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.cpuTemp.isEnabled || overlaySettings.sensors.cpuUsage.isEnabled) {
        Pill(
            title = "CPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.cpuTemp.isEnabled) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.cpuTemp.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "°C",
                    label = { "${it.toInt()}" },
                    boundaries = overlaySettings.sensors.cpuTemp.boundaries,
                )
            }

            if (overlaySettings.sensors.cpuUsage.isEnabled) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.cpuUsage.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "%",
                    label = { String.format("%02d", it.toInt(), Locale.US) },
                    boundaries = overlaySettings.sensors.cpuUsage.boundaries,
                )
            }
        }
    }
}
