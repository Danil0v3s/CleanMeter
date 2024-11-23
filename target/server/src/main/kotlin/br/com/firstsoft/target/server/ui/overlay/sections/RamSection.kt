package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hardwaremonitor.HardwareMonitorData
import br.com.firstsoft.core.common.hardwaremonitor.RamUsage
import br.com.firstsoft.core.common.hardwaremonitor.RamUsagePercent
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import java.util.*

@Composable
internal fun RamSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.ramUsage.isEnabled) {
        Pill(
            title = "RAM",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            Progress(
                value = data.RamUsagePercent / 100f,
                label = String.format("%02.1f", data.RamUsage / 1000, Locale.US),
                unit = "GB",
                progressType = overlaySettings.progressType
            )
        }
    }
}