package app.cleanmeter.target.desktop.ui.settings.tabs.style

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.SliderThumb
import app.cleanmeter.target.desktop.ui.components.coercedValueAsFraction
import app.cleanmeter.target.desktop.ui.components.drawTrack
import app.cleanmeter.target.desktop.ui.components.section.CollapsibleSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Opacity(
    overlaySettings: OverlaySettings,
    onOpacityChange: (Float) -> Unit
) {
    CollapsibleSection(title = "OPACITY") {
        Column {
            val inactiveTrackColor = LocalColorScheme.current.background.surfaceSunkenSubtle
            val activeTrackColor = LocalColorScheme.current.background.brand
            val inactiveTickColor = LocalColorScheme.current.background.surfaceSunken
            val activeTickColor = LocalColorScheme.current.background.brandHover
            Slider(
                value = overlaySettings.opacity,
                onValueChange = {
                    onOpacityChange(it.coerceIn(0f, 1f))
                },
                steps = 9,
                track = { sliderState ->
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        drawTrack(
                            tickFractions = FloatArray(sliderState.steps + 2) { it.toFloat() / (sliderState.steps + 1) },
                            activeRangeStart = 0f,
                            activeRangeEnd = sliderState.coercedValueAsFraction,
                            inactiveTrackColor = inactiveTrackColor,
                            activeTrackColor = activeTrackColor,
                            inactiveTickColor = inactiveTickColor,
                            activeTickColor = activeTickColor,
                        )
                    }
                },
                thumb = {
                    SliderThumb()
                }
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(painterResource("icons/no_brightness.svg"), "", tint = LocalColorScheme.current.icon.bolderActive)
                Icon(painterResource("icons/mid_brightness.svg"), "", tint = LocalColorScheme.current.icon.bolderActive)
                Icon(painterResource("icons/full_brightness.svg"), "", tint = LocalColorScheme.current.icon.bolderActive)
            }
        }
    }
}