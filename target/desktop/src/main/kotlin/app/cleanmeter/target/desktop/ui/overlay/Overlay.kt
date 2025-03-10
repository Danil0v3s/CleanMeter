package app.cleanmeter.target.desktop.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.AppTheme

@Composable
fun Overlay(
    data: HardwareMonitorData?,
    overlaySettings: OverlaySettings,
) = AppTheme(false) {
    if (
        listOf(
            overlaySettings.sensors.framerate,
            overlaySettings.sensors.frametime,
            overlaySettings.sensors.cpuTemp,
            overlaySettings.sensors.gpuTemp,
            overlaySettings.sensors.cpuUsage,
            overlaySettings.sensors.gpuUsage,
            overlaySettings.sensors.vramUsage,
            overlaySettings.sensors.ramUsage,
            overlaySettings.sensors.cpuConsumption,
            overlaySettings.sensors.gpuConsumption
        ).all { !it.isEnabled }
    ) {
        return@AppTheme
    }

    val alignment = when (overlaySettings.positionIndex) {
        0 -> Alignment.TopStart
        1 -> Alignment.TopCenter
        2 -> Alignment.TopEnd
        3 -> Alignment.BottomStart
        4 -> Alignment.BottomCenter
        5 -> Alignment.BottomEnd
        6 -> when {
            overlaySettings.positionX <= 40 -> Alignment.CenterStart
            overlaySettings.positionX >= 60 -> Alignment.CenterEnd
            else -> Alignment.Center
        }
        else -> Alignment.Center
    }
    Box(modifier = Modifier.fillMaxSize().alpha(overlaySettings.opacity), contentAlignment = alignment) {
        OverlayUi(
            data = data,
            overlaySettings = overlaySettings
        )
    }
}
