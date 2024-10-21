package br.com.firstsoft.target.server.ui.components

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.ClearGray
import br.com.firstsoft.target.server.ui.ColorTokens.Green
import br.com.firstsoft.target.server.ui.ColorTokens.Red
import br.com.firstsoft.target.server.ui.ColorTokens.Yellow
import ui.app.OverlaySettings
import kotlin.math.abs

@Composable
fun Progress(
    value: Float,
    label: String,
    unit: String,
    progressType: OverlaySettings.ProgressType
) {
    val color = when {
        value > 0.7f && value < 0.9f -> Red
        value > 0.5f && value < 0.7f -> Yellow
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
                repeat(10) {
                    val barColor = if (value.times(10) > abs(it - 9)) color else Color.Transparent

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

@Composable
private fun ProgressUnit(unit: String) {
    Text(
        text = unit,
        fontSize = 10.sp,
        color = Color.White,
        lineHeight = 0.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun ProgressLabel(label: String) {
    Text(
        text = label,
        fontSize = 16.sp,
        color = Color.White,
        lineHeight = 0.sp,
        fontWeight = FontWeight.Normal,
    )
}