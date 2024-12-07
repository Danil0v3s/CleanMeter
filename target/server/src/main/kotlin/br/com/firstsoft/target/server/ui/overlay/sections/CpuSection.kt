package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.components.CustomReadingProgress
import br.com.firstsoft.target.server.ui.components.Pill
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
                    label = { "${it.toInt()}" }
                )
            }

            if (overlaySettings.sensors.cpuUsage.isEnabled) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.cpuUsage.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "%",
                    label = { String.format("%02d", it.toInt(), Locale.US) }
                )
            }
        }
    }
}
