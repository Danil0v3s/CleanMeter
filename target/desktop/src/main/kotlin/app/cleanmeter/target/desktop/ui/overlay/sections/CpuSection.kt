package app.cleanmeter.target.desktop.ui.overlay.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.common.hardwaremonitor.FPS
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.common.hardwaremonitor.getReading
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.CustomReadingProgress
import app.cleanmeter.target.desktop.ui.components.Pill
import app.cleanmeter.target.desktop.ui.components.ProgressLabel
import app.cleanmeter.target.desktop.ui.components.ProgressUnit
import java.util.*

@Composable
internal fun CpuSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.cpuTemp.isValid() || overlaySettings.sensors.cpuUsage.isValid()) {
        Pill(
            title = "CPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.cpuTemp.isValid()) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.cpuTemp.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "°C",
                    label = { "${it.toInt()}" },
                    boundaries = overlaySettings.sensors.cpuTemp.boundaries,
                )
            }

            if (overlaySettings.sensors.cpuUsage.isValid()) {
                CustomReadingProgress(
                    data = data,
                    customReadingId = overlaySettings.sensors.cpuUsage.customReadingId,
                    progressType = overlaySettings.progressType,
                    progressUnit = "%",
                    label = { String.format("%02d", it.toInt(), Locale.US) },
                    boundaries = overlaySettings.sensors.cpuUsage.boundaries,
                )
            }

            if (overlaySettings.sensors.cpuConsumption.isValid()) {
                val reading = data.getReading(overlaySettings.sensors.cpuConsumption.customReadingId)
                val value = (reading?.Value ?: 1f).coerceAtLeast(1f).toInt()
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp).padding(bottom = 2.dp)) {
                    ProgressLabel("$value")
                    ProgressUnit("W")
                }
            }
        }
    }
}
