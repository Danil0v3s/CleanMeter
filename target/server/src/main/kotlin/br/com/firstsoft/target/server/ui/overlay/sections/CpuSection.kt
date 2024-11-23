package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hardwaremonitor.getReading
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import java.util.*

@Composable
internal fun CpuSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.cpuTemp.isEnabled || overlaySettings.sensors.cpuUsage.isEnabled) {
        Pill(
            title = "CPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.cpuTemp.isEnabled) {
                val cpuTemp = data.getReading(overlaySettings.sensors.cpuTemp.customReadingId)
                val cpuTempValue = (cpuTemp?.Value ?: 1f).coerceAtLeast(1f).toInt()

                Progress(
                    value = cpuTempValue / 100f,
                    label = "$cpuTempValue",
                    unit = "°C",
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.sensors.cpuUsage.isEnabled) {
                val cpuUsage = data.getReading(overlaySettings.sensors.cpuUsage.customReadingId)
                val cpuUsageValue = (cpuUsage?.Value ?: 1f).coerceAtLeast(1f)
                Progress(
                    value = cpuUsageValue / 100f,
                    label = String.format("%02d", cpuUsageValue.toInt(), Locale.US),
                    unit = "%",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}