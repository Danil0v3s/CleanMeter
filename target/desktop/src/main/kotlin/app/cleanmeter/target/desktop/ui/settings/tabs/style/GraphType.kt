package app.cleanmeter.target.desktop.ui.settings.tabs.style

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.StyleCard
import app.cleanmeter.target.desktop.ui.components.section.ToggleSection

@Composable
internal fun GraphType(
    overlaySettings: OverlaySettings,
    onGraphTypeChange: (OverlaySettings.ProgressType) -> Unit
) {
    ToggleSection(
        title = "GRAPH",
        isEnabled = overlaySettings.progressType != OverlaySettings.ProgressType.None,
        onSwitchToggle = {
            onGraphTypeChange(if (!it) OverlaySettings.ProgressType.None else OverlaySettings.ProgressType.Circular)
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StyleCard(
                label = "Ring graph",
                isSelected = overlaySettings.progressType == OverlaySettings.ProgressType.Circular,
                modifier = Modifier.weight(.5f).height(210.dp),
                onClick = { onGraphTypeChange(OverlaySettings.ProgressType.Circular) },
                content = {
                    Image(
                        painter = painterResource("icons/rings.png"),
                        contentDescription = "Horizontal image"
                    )
                }
            )

            StyleCard(
                label = "Bar graph",
                isSelected = overlaySettings.progressType == OverlaySettings.ProgressType.Bar,
                modifier = Modifier.weight(.5f).height(210.dp),
                onClick = { onGraphTypeChange(OverlaySettings.ProgressType.Bar) },
                content = {
                    Image(
                        painter = painterResource("icons/bars.png"),
                        contentDescription = "Vertical image",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            )
        }
    }
}
