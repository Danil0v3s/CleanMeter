package ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.firstsoft.target.server.ui.AppTheme
import mahm.MahmReader
import ui.OverlayUi


@Composable
fun Overlay(
    mahmReader: MahmReader = MahmReader(),
    overlaySettings: OverlaySettings,
) = AppTheme {
    val arrangement = when (overlaySettings.positionIndex) {
        0 -> Alignment.TopStart
        1 -> Alignment.TopCenter
        2 -> Alignment.TopEnd
        3 -> Alignment.BottomStart
        4 -> Alignment.BottomCenter
        5 -> Alignment.BottomEnd
        else -> Alignment.Center
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = arrangement) {
        OverlayUi(mahmReader, overlaySettings)
    }
}
