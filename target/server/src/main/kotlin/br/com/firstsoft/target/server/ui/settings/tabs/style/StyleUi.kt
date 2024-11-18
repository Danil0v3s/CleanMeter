package br.com.firstsoft.target.server.ui.settings.tabs.style

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import br.com.firstsoft.target.server.model.OverlaySettings

@Composable
fun StyleUi(
    overlaySettings: OverlaySettings,
    onOverlayPositionIndex: (Int) -> Unit,
    onOverlayCustomPosition: (IntOffset, Boolean) -> Unit,
    onLayoutChange: (Boolean) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onGraphTypeChange: (OverlaySettings.ProgressType) -> Unit,
    getOverlayPosition: () -> IntOffset,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Position(overlaySettings, onOverlayPositionIndex, onOverlayCustomPosition, getOverlayPosition)

    Orientation(overlaySettings, onLayoutChange)

    Opacity(overlaySettings, onOpacityChange)

    GraphType(overlaySettings, onGraphTypeChange)
}











