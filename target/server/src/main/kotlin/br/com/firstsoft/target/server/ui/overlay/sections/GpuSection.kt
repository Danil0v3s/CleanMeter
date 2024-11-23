package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hardwaremonitor.getReading
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.components.CustomReadingProgress
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
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
                    label = { "${it.toInt()}" }
                )
            }

            if (overlaySettings.sensors.gpuUsage.isEnabled) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.gpuUsage.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "%",
                    label = { String.format("%02d", it.toInt(), Locale.US) }
                )
            }

            if (overlaySettings.sensors.vramUsage.isEnabled) {
                val vramUsage = data.getReading(overlaySettings.sensors.vramUsage.customReadingId)?.Value?.coerceAtLeast(1f) ?: 1f
                val totalVramUsed = data.getReading(overlaySettings.sensors.totalVramUsed.customReadingId)?.Value?.coerceAtLeast(1f) ?: 1f

                Progress(
                    value = vramUsage / 100f,
                    label = String.format("%02.1f", totalVramUsed / 1000, Locale.US),
                    unit = "GB",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}