package br.com.firstsoft.target.server.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import br.com.firstsoft.target.server.ui.ColorTokens.ClearGray
import br.com.firstsoft.target.server.ui.ColorTokens.Green
import br.com.firstsoft.target.server.ui.ColorTokens.Red
import br.com.firstsoft.target.server.ui.ColorTokens.Yellow
import br.com.firstsoft.target.server.model.OverlaySettings
import kotlin.math.abs

@Composable
fun Progress(
    value: Float,
    label: String,
    unit: String,
    progressType: OverlaySettings.ProgressType
) {
    val color = when {
        value > 0.8f -> Red
        value in 0.6f..0.8f -> Yellow
        else -> Green
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        when (progressType) {
            OverlaySettings.ProgressType.Circular -> CircularProgressIndicator(
                progress = value,
                modifier = Modifier.size(24.dp),
                color = color,
                backgroundColor = ClearGray,
                strokeCap = StrokeCap.Round,
                strokeWidth = 3.dp
            )

            OverlaySettings.ProgressType.Bar -> Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                val integerValue = value.times(10).toInt()

                repeat(10) {
                    val inverseValue = abs(it - 9)

                    val barColor = if (integerValue < inverseValue) Color.Transparent else when {
                        inverseValue >= 8 -> Red
                        inverseValue in 5..7 -> Yellow
                        else -> Green
                    }

                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(1.dp)
                            .background(barColor, RoundedCornerShape(50))
                    )
                }
            }

            OverlaySettings.ProgressType.None -> Unit
        }

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp)) {
            ProgressLabel(label)
            ProgressUnit(unit)
        }
    }
}



@Preview
@Composable
private fun ProgressPreview() {
    Row(modifier = Modifier.background(Color.Black)) {
        Progress(
            value = 0.5f,
            label = "05",
            progressType = OverlaySettings.ProgressType.Bar,
            unit = "C"
        )

        Progress(
            value = 0.6f,
            label = "06",
            progressType = OverlaySettings.ProgressType.Bar,
            unit = "C"
        )

        Progress(
            value = 0.7f,
            label = "07",
            progressType = OverlaySettings.ProgressType.Bar,
            unit = "C"
        )

        Progress(
            value = 0.8f,
            label = "08",
            progressType = OverlaySettings.ProgressType.Bar,
            unit = "C"
        )

        Progress(
            value = 0.9f,
            label = "09",
            progressType = OverlaySettings.ProgressType.Bar,
            unit = "C"
        )
    }
}