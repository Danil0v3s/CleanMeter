package app.cleanmeter.target.desktop.ui.overlay.sections

import androidx.compose.runtime.Composable
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.common.hardwaremonitor.getReading
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.CustomReadingProgress
import app.cleanmeter.target.desktop.ui.components.Pill
import app.cleanmeter.target.desktop.ui.components.Progress
import java.util.*

@Composable
internal fun GpuSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.gpuTemp.isEnabled || overlaySettings.sensors.gpuUsage.isEnabled || overlaySettings.sensors.vramUsage.isEnabled) {
        Pill(
            title = "GPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.gpuTemp.isEnabled) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.gpuTemp.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "°C",
                    label = { "${it.toInt()}" },
                    boundaries = overlaySettings.sensors.gpuTemp.boundaries,
                )
            }

            if (overlaySettings.sensors.gpuUsage.isEnabled) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.gpuUsage.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "%",
                    label = { String.format("%02d", it.toInt(), Locale.US) },
                    boundaries = overlaySettings.sensors.gpuUsage.boundaries,
                )
            }

            if (overlaySettings.sensors.vramUsage.isEnabled) {
                val vramUsage = data.getReading(overlaySettings.sensors.vramUsage.customReadingId, "memory")?.Value?.coerceAtLeast(1f) ?: 1f
                val totalVramUsed = data.getReading(overlaySettings.sensors.totalVramUsed.customReadingId)?.Value?.coerceAtLeast(1f) ?: 1f

                Progress(
                    value = vramUsage / 100f,
                    label = String.format("%02.1f", totalVramUsed / 1000, Locale.US),
                    unit = "GB",
                    progressType = overlaySettings.progressType,
                    boundaries = overlaySettings.sensors.vramUsage.boundaries,
                )
            }
        }
    }
}