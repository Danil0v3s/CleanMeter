package app.cleanmeter.target.desktop.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.ColorTokens.ClearGray
import app.cleanmeter.target.desktop.ui.ColorTokens.Green
import app.cleanmeter.target.desktop.ui.ColorTokens.Red
import app.cleanmeter.target.desktop.ui.ColorTokens.Yellow
import kotlin.math.abs

@Composable
fun Progress(
    value: Float,
    label: String,
    unit: String,
    progressType: OverlaySettings.ProgressType,
    boundaries: OverlaySettings.Sensor.GraphSensor.Boundaries
) {
    val color = when {
        value in 0f..boundaries.low.div(100f) -> Green
        value in boundaries.low.div(100f)..boundaries.medium.div(100f) -> Yellow
        value > boundaries.medium.div(100f) -> Red
        else -> White
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

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp).padding(bottom = 2.dp)) {
            ProgressLabel(label)
            ProgressUnit(unit)
        }
    }
}

@Preview
@Composable
private fun ProgressPreview() {
    Column {
        ProgressRow(OverlaySettings.ProgressType.Bar)
        ProgressRow(OverlaySettings.ProgressType.Circular)
    }
}

@Composable
private fun ProgressRow(progressType :OverlaySettings.ProgressType) {
    Row(modifier = Modifier.background(Color.Black)) {
        Progress(
            value = 0.12f,
            label = "12",
            progressType = progressType,
            unit = "C",
            boundaries = OverlaySettings.Sensor.GraphSensor.Boundaries()
        )

        Progress(
            value = 0.53f,
            label = "53",
            progressType = progressType,
            unit = "C",
            boundaries = OverlaySettings.Sensor.GraphSensor.Boundaries()
        )

        Progress(
            value = 0.67f,
            label = "67",
            progressType = progressType,
            unit = "C",
            boundaries = OverlaySettings.Sensor.GraphSensor.Boundaries()
        )

        Progress(
            value = 0.72f,
            label = "72",
            progressType = progressType,
            unit = "C",
            boundaries = OverlaySettings.Sensor.GraphSensor.Boundaries()
        )

        Progress(
            value = 0.86f,
            label = "86",
            progressType = progressType,
            unit = "C",
            boundaries = OverlaySettings.Sensor.GraphSensor.Boundaries()
        )

        Progress(
            value = 0.99f,
            label = "99",
            progressType = progressType,
            unit = "C",
            boundaries = OverlaySettings.Sensor.GraphSensor.Boundaries()
        )
    }
}