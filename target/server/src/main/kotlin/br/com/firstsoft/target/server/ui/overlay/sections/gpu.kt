package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.common.hwinfo.VramUsage
import br.com.firstsoft.core.common.hwinfo.getReading
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import java.util.*

@Composable
internal fun gpu(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.gpuTemp || overlaySettings.gpuUsage || overlaySettings.vramUsage) {
        Pill(
            title = "GPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.gpuTemp) {
                val reading = data.getReading(overlaySettings.gpuTempReadingId.readingId, overlaySettings.gpuTempReadingId.sensorIndex)
                val readingValue = (reading?.value ?: 1f).coerceAtLeast(1f).toInt()

                Progress(
                    value = readingValue / 100f,
                    label = "$readingValue",
                    unit = reading?.szUnit.orEmpty(),
                    progressType = overlaySettings.progressType
                )
            }

            if (overlaySettings.gpuUsage) {
                val reading = data.getReading(overlaySettings.gpuUsageReadingId.readingId, overlaySettings.gpuUsageReadingId.sensorIndex)
                val readingValue = (reading?.value ?: 1f).coerceAtLeast(1f).toInt()

                Progress(
                    value = readingValue / 100f,
                    label = String.format("%02d", readingValue, Locale.US),
                    unit = "%",
                    progressType = overlaySettings.progressType
                )
            }

            if (overlaySettings.vramUsage) {
                val reading = data.getReading(overlaySettings.vramUsageReadingId.readingId, overlaySettings.vramUsageReadingId.sensorIndex)
                val readingValue = (reading?.value ?: 1f).coerceAtLeast(1f).toInt()

                val committedReading = data.getReading(overlaySettings.vramCommittedReadingId.readingId, overlaySettings.vramCommittedReadingId.sensorIndex)
                val commitedReadingValue = (committedReading?.value ?: 1f).coerceAtLeast(1f).toInt()

                Progress(
                    value = readingValue / 100f,
                    label = String.format("%02.1f", commitedReadingValue / 1000f, Locale.US),
                    unit = "GB",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}