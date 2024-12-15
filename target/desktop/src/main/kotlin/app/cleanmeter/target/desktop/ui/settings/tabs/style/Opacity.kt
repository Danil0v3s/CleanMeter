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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.ColorTokens.AlmostVisibleGray
import app.cleanmeter.target.desktop.ui.ColorTokens.BackgroundOffWhite
import app.cleanmeter.target.desktop.ui.ColorTokens.DarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.LabelGray
import app.cleanmeter.target.desktop.ui.components.section.CollapsibleSection
import app.cleanmeter.target.desktop.ui.components.SliderThumb
import app.cleanmeter.target.desktop.ui.components.coercedValueAsFraction
import app.cleanmeter.target.desktop.ui.components.drawTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Opacity(
    overlaySettings: OverlaySettings,
    onOpacityChange: (Float) -> Unit
) {
    CollapsibleSection(title = "OPACITY") {
        Column {
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
                            FloatArray(sliderState.steps + 2) { it.toFloat() / (sliderState.steps + 1) },
                            0f,
                            sliderState.coercedValueAsFraction,
                            BackgroundOffWhite,
                            DarkGray,
                            AlmostVisibleGray,
                            Color.White,
                        )
                    }
                },
                thumb = {
                    SliderThumb()
                }
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(painterResource("icons/no_brightness.svg"), "", tint = LabelGray)
                Icon(painterResource("icons/mid_brightness.svg"), "", tint = LabelGray)
                Icon(painterResource("icons/full_brightness.svg"), "", tint = LabelGray)
            }
        }
    }
}