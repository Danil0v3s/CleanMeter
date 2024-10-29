package ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import br.com.firstsoft.target.server.ui.AppTheme
import hwinfo.HwInfoReader
import ui.OverlayUi


@Composable
fun Overlay(
    hwInfoReader: HwInfoReader = HwInfoReader(),
    overlaySettings: OverlaySettings,
) = AppTheme {
    if (listOf(
            overlaySettings.fps,
            overlaySettings.frametime,
            overlaySettings.cpuTemp,
            overlaySettings.gpuTemp,
            overlaySettings.cpuUsage,
            overlaySettings.gpuUsage,
            overlaySettings.vramUsage,
            overlaySettings.ramUsage
        ).all { !it }
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
            reader = hwInfoReader,
            overlaySettings = overlaySettings
        )
    }
}
