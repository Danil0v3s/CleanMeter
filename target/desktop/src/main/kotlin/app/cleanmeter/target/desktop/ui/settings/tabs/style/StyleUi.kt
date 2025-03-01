package app.cleanmeter.target.desktop.ui.settings.tabs.style

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.section.DropdownSection
import java.awt.GraphicsEnvironment

@Composable
fun StyleUi(
    overlaySettings: OverlaySettings,
    onOverlayPositionIndex: (Int) -> Unit,
    onOverlayCustomPosition: (IntOffset, Boolean) -> Unit,
    onLayoutChange: (Boolean) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onGraphTypeChange: (OverlaySettings.ProgressType) -> Unit,
    onOverlayCustomPositionEnable: (Boolean) -> Unit,
    onDisplaySelect: (Int) -> Unit,
    getOverlayPosition: () -> IntOffset,
) = Column(
    modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val screenDevices = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices }

    Position(
        overlaySettings = overlaySettings,
        onOverlayPositionIndex = onOverlayPositionIndex,
        onOverlayCustomPosition = onOverlayCustomPosition,
        getOverlayPosition = getOverlayPosition,
        onOverlayCustomPositionEnable = onOverlayCustomPositionEnable,
    )

    Orientation(
        overlaySettings = overlaySettings,
        onLayoutChange = onLayoutChange
    )

    Opacity(
        overlaySettings = overlaySettings,
        onOpacityChange = onOpacityChange
    )

    GraphType(
        overlaySettings = overlaySettings,
        onGraphTypeChange = onGraphTypeChange
    )

    DropdownSection(
        title = "MONITOR",
        options = screenDevices.map { it.defaultConfiguration.device.iDstring },
        selectedIndex = overlaySettings.selectedDisplayIndex,
        onValueChanged = { onDisplaySelect(it) }
    )
}
