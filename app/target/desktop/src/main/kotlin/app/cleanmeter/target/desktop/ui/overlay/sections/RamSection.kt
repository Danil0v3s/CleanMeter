package app.cleanmeter.target.desktop.ui.overlay.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.common.hardwaremonitor.RamUsage
import app.cleanmeter.core.common.hardwaremonitor.RamUsagePercent
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.Pill
import app.cleanmeter.target.desktop.ui.components.Progress
import java.util.*

@Composable
internal fun RamSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    AnimatedVisibility(
        visible = overlaySettings.sensors.ramUsage.isEnabled,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
    ) {
        Pill(
            title = "RAM",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            Progress(
                value = data.RamUsagePercent,
                label = String.format("%02.1f", data.RamUsage, Locale.US),
                unit = "GB",
                progressType = overlaySettings.progressType,
                boundaries = overlaySettings.sensors.ramUsage.boundaries,
            )
        }
    }
}