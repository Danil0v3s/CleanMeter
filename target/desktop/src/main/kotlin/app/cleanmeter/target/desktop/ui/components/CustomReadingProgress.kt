package app.cleanmeter.target.desktop.ui.components

import androidx.compose.runtime.Composable
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.common.hardwaremonitor.getReading
import app.cleanmeter.target.desktop.model.OverlaySettings

@Composable
internal fun CustomReadingProgress(
    data: HardwareMonitorData,
    label: (Float) -> String,
    customReadingId: String,
    progressType: OverlaySettings.ProgressType,
    progressUnit: String,
    boundaries: OverlaySettings.Sensor.GraphSensor.Boundaries
) {
    val reading = data.getReading(customReadingId)
    val value = (reading?.Value ?: 1f).coerceAtLeast(1f)

    Progress(
        value = value / 100f,
        label = label(value),
        unit = progressUnit,
        progressType = progressType,
        boundaries = boundaries
    )
}