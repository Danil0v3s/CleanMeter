package br.com.firstsoft.target.server.ui.settings.tabs.style

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
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.BackgroundOffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.components.CollapsibleSection
import br.com.firstsoft.target.server.ui.components.SliderThumb
import br.com.firstsoft.target.server.ui.components.coercedValueAsFraction
import br.com.firstsoft.target.server.ui.components.drawTrack

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