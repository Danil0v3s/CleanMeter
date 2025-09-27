package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.settings.SensorType

@Composable
internal fun SensorBoundaryInput(
    sensor: OverlaySettings.Sensor,
    sensorType: SensorType,
    onBoundaryChange: (SensorType, OverlaySettings.Sensor.GraphSensor.Boundaries) -> Unit,
) {
    if (sensor !is OverlaySettings.Sensor.GraphSensor) return

    val colorScheme = LocalColorScheme.current
    val unit = when (sensorType) {
        SensorType.CpuTemp,
        SensorType.GpuTemp -> "°"

        SensorType.CpuUsage,
        SensorType.GpuUsage,
        SensorType.VramUsage,
        SensorType.RamUsage -> "%"

        else -> ""
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 12.dp)
            .drawWithCache {
                onDrawWithContent {
                    drawRoundRect(
                        color = colorScheme.border.subtle,
                        cornerRadius = CornerRadius(100f, 100f),
                        size = Size(2f, (size.height).coerceAtLeast(0f)),
                        topLeft = Offset(0f, 0f)
                    )
                    drawContent()
                }
            }
            .padding(start = 20.dp, top = 12.dp)
            .fillMaxWidth()
            .background(LocalColorScheme.current.background.surfaceSunkenSubtle, RoundedCornerShape(8.dp)).padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BoundaryInput(
                    label = "Low",
                    labelColor = LocalColorScheme.current.background.successHover,
                    minValue = 0,
                    maxValue = sensor.boundaries.low,
                    onMaxValueChange = { onBoundaryChange(sensorType, sensor.boundaries.copy(low = it)) },
                    unit = unit
                )

                BoundaryInput(
                    label = "Medium",
                    labelColor = LocalColorScheme.current.background.warningHover,
                    minValue = sensor.boundaries.low,
                    maxValue = sensor.boundaries.medium,
                    onMaxValueChange = { onBoundaryChange(sensorType, sensor.boundaries.copy(medium = it)) },
                    unit = unit
                )

                BoundaryInput(
                    label = "High",
                    labelColor = LocalColorScheme.current.background.dangerHover,
                    minValue = sensor.boundaries.medium,
                    maxValue = sensor.boundaries.high,
                    onMaxValueChange = { onBoundaryChange(sensorType, sensor.boundaries.copy(high = it)) },
                    unit = unit
                )
            }
            Disclaimer("Colors are visible only when the graph is enabled in the style settings")
        }
    }
}

@Composable
private fun RowScope.BoundaryInput(
    label: String,
    labelColor: Color,
    unit: String,
    minValue: Int,
    maxValue: Int,
    onMaxValueChange: (Int) -> Unit,
) {
    val borderColor = LocalColorScheme.current.border.bolder

    Column(modifier = Modifier.weight(0.33f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(6.dp).background(labelColor, CircleShape))
            Text(
                text = label,
                style = LocalTypography.current.labelLMedium,
                color = LocalColorScheme.current.text.heading,
                modifier = Modifier.wrapContentHeight()
            )
        }
        Row(
            modifier = Modifier
                .height(40.dp)
                .border(1.dp, LocalColorScheme.current.border.bolder, RoundedCornerShape(8.dp))
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()

                        drawRect(
                            color = borderColor,
                            topLeft = Offset((size.width / 2f) - 0.5f, 0f),
                            size = Size(1f, size.height),
                        )
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .padding(12.dp)
            ) {
                Text(
                    text = "$minValue$unit",
                    style = LocalTypography.current.labelLMedium,
                    color = LocalColorScheme.current.text.paragraph2,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.wrapContentHeight()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .background(LocalColorScheme.current.background.surfaceRaised)
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = "$maxValue",
                    textStyle = LocalTypography.current.labelLMedium.copy(
                        lineHeight = 2.sp,
                        textAlign = TextAlign.Start,
                        color = LocalColorScheme.current.text.heading,
                    ),
                    visualTransformation = remember {
                        VisualTransformation { text ->
                            TransformedText(
                                text = buildAnnotatedString { append("$text$unit") },
                                offsetMapping = object : OffsetMapping {
                                    override fun originalToTransformed(offset: Int): Int {
                                        return if (offset >= 1) offset + 1 else offset
                                    }

                                    override fun transformedToOriginal(offset: Int): Int {
                                        return if (offset > 1) offset - 1 else offset
                                    }
                                }
                            )
                        }
                    },
                    singleLine = true,
                    onValueChange = {
                        val intValue = it.toIntOrNull()
                        if (intValue != null && intValue <= 100) {
                            onMaxValueChange(intValue)
                        }
                    },
                    modifier = Modifier.wrapContentHeight().align(Alignment.CenterStart),
                )
            }

        }
    }
}