package br.com.firstsoft.target.server.ui.components

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hardwaremonitor.getReading
import br.com.firstsoft.target.server.model.OverlaySettings

@Composable
internal fun CustomReadingProgress(
    data: HardwareMonitorData,
    label: (Float) -> String,
    customReadingId: String,
    progressType: OverlaySettings.ProgressType,
    progressUnit: String,
) {
    val reading = data.getReading(customReadingId)
    val value = (reading?.Value ?: 1f).coerceAtLeast(1f)

    Progress(
        value = value / 100f,
        label = label(value),
        unit = progressUnit,
        progressType = progressType
    )
}