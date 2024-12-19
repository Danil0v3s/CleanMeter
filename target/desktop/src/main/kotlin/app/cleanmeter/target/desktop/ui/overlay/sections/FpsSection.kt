package app.cleanmeter.target.desktop.ui.overlay.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.common.hardwaremonitor.FPS
import app.cleanmeter.core.common.hardwaremonitor.Frametime
import app.cleanmeter.core.common.hardwaremonitor.HardwareMonitorData
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.target.desktop.model.OverlaySettings
import app.cleanmeter.target.desktop.ui.components.Pill
import app.cleanmeter.target.desktop.ui.overlay.conditional
import java.util.*

@Composable
internal fun FpsSection(overlaySettings: OverlaySettings, data: HardwareMonitorData) {
    if (overlaySettings.sensors.framerate.isEnabled || overlaySettings.sensors.frametime.isEnabled) {
        if (overlaySettings.isHorizontal) {
            Pill(
                title = "FPS",
                isHorizontal = true,
            ) {
                if (overlaySettings.sensors.framerate.isEnabled) {
                    Text(
                        text = "${data.FPS}",
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.width(50.dp)
                    )
                }

                if (overlaySettings.sensors.frametime.isEnabled) {
                    FrametimeGraph(data, true)
                    Text(
                        text = "${String.format("%02.01f", data.Frametime, Locale.US)} ms",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.width(50.dp).padding(bottom = 2.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "FPS",
                        fontSize = 10.sp,
                        color = LocalColorScheme.current.text.inverseSubtler,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (overlaySettings.sensors.framerate.isEnabled) {
                            Text(
                                text = "${data.FPS}",
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.width(50.dp)
                            )
                        }

                        if (overlaySettings.sensors.frametime.isEnabled) {
                            Text(
                                text = "${String.format("%02.01f", data.Frametime, Locale.US)} ms",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.width(50.dp)
                            )
                        }
                    }
                }

                if (overlaySettings.sensors.frametime.isEnabled) {
                    FrametimeGraph(data = data, isHorizontal = false)
                }
            }
        }
    }
}

@Composable
private fun FrametimeGraph(data: HardwareMonitorData, isHorizontal: Boolean) {
    val largestFrametime = remember { mutableFloatStateOf(0f) }
    val listSize = 30
    val frametimePoints = remember { mutableStateListOf<Float>() }

    val frametimePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Color.White
            strokeWidth = 1f
            blendMode = BlendMode.DstAtop
        }
    }

    LaunchedEffect(data) {
        if (data.Frametime > largestFrametime.floatValue) {
            largestFrametime.floatValue = data.Frametime
        }
        frametimePoints.add(1f - (data.Frametime / largestFrametime.floatValue))
        if (frametimePoints.size > listSize) frametimePoints.removeFirst()
    }

    Box(modifier = Modifier
        .conditional(
            predicate = isHorizontal,
            ifTrue = { width(100.dp).height(45.dp) },
            ifFalse = { fillMaxWidth().height(30.dp) },
        )
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            val colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Transparent)
            val frametimeZip = frametimePoints.zipWithNext()

            drawIntoCanvas { canvas ->
                drawLine(frametimeZip, listSize, canvas, frametimePaint)
            }

            drawRect(brush = Brush.horizontalGradient(colors), blendMode = BlendMode.DstIn)
        })
}
