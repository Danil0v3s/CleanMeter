package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import br.com.firstsoft.target.server.ui.ColorTokens.OffWhite
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import mahm.CpuTemp
import mahm.CpuTempUnit
import mahm.CpuUsage
import mahm.Data
import mahm.FPS
import mahm.Frametime
import mahm.GpuTemp
import mahm.GpuTempUnit
import mahm.GpuUsage
import mahm.MahmReader
import mahm.RamUsage
import mahm.RamUsagePercent
import mahm.VramUsage
import mahm.VramUsagePercent
import ui.app.OverlaySettings



inline fun Modifier.conditional(
    predicate: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (predicate) ifTrue(this) else ifFalse(this)

@Composable
fun OverlayUi(
    reader: MahmReader,
    overlaySettings: OverlaySettings,
) {

    val data by reader.currentData.collectAsState(null)

    if (data == null) {
        Text("Unable to read data...")
        return
    }

    if (overlaySettings.isHorizontal) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .background(Color.Black.copy(alpha = 0.36f), CircleShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Content(
                data!!,
                overlaySettings = overlaySettings,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.36f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Content(
                data!!,
                overlaySettings = overlaySettings,
            )
        }
    }
}

@Composable
fun Content(data: Data, overlaySettings: OverlaySettings) {
    if (overlaySettings.isHorizontal) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fps(overlaySettings, data)
            gpu(overlaySettings, data)
            cpu(overlaySettings, data)
            ram(overlaySettings, data)
        }
    } else {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Layout(
                content = {
                    fps(overlaySettings, data)
                    gpu(overlaySettings, data)
                    cpu(overlaySettings, data)
                    ram(overlaySettings, data)
                },
                measurePolicy = object : MeasurePolicy {
                    override fun MeasureScope.measure(
                        measurables: List<Measurable>,
                        constraints: Constraints
                    ): MeasureResult {
                        val maxWidth = maxIntrinsicWidth(measurables, constraints.maxHeight)
                        val placeables = measurables.map { it.measure(Constraints.fixedWidth(maxWidth)) }
                        val height = placeables.sumOf { it.height } + 4.dp.roundToPx() * (placeables.size - 1)

                        return layout(maxWidth, height) {
                            var yPosition = 0
                            placeables.forEach { placeable ->
                                placeable.placeRelative(x = 0, y = yPosition)
                                yPosition += placeable.height + 4.dp.roundToPx()
                            }
                        }
                    }

                    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
                        measurables: List<IntrinsicMeasurable>,
                        height: Int
                    ): Int {
                        return measurables.map { it.maxIntrinsicWidth(height) }.maxOf { it }
                    }
                })
        }
    }
}

@Composable
private fun ram(overlaySettings: OverlaySettings, data: Data) {
    if (overlaySettings.ramUsage) {
        Pill(
            title = "RAM",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            Progress(
                value = data.RamUsagePercent / 100f,
                label = String.format("%02.1f", data.RamUsage / 1000),
                unit = "GB",
                progressType = overlaySettings.progressType
            )
        }
    }
}

@Composable
private fun cpu(overlaySettings: OverlaySettings, data: Data) {
    if (overlaySettings.cpuTemp || overlaySettings.cpuUsage) {
        Pill(
            title = "CPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.cpuTemp) {
                Progress(
                    value = data.CpuTemp / 100f,
                    label = "${data.CpuTemp}",
                    unit = data.CpuTempUnit,
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.cpuUsage) {
                Progress(
                    value = data.CpuUsage / 100f,
                    label = String.format("%02d", data.CpuUsage),
                    unit = "%",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}

@Composable
private fun gpu(overlaySettings: OverlaySettings, data: Data) {
    if (overlaySettings.gpuTemp || overlaySettings.gpuUsage || overlaySettings.vramUsage) {
        Pill(
            title = "GPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.gpuTemp) {
                Progress(
                    value = data.GpuTemp / 100f,
                    label = "${data.GpuTemp}",
                    unit = data.GpuTempUnit,
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.gpuUsage) {
                Progress(
                    value = data.GpuUsage / 100f,
                    label = String.format("%02d", data.GpuUsage),
                    unit = "%",
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.vramUsage) {
                Progress(
                    value = data.VramUsagePercent / 100f,
                    label = String.format("%02.1f", data.VramUsage / 1000),
                    unit = "GB",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}

@Composable
private fun fps(overlaySettings: OverlaySettings, data: Data) {
    if (overlaySettings.fps || overlaySettings.frametime) {

        if (overlaySettings.isHorizontal) {
            Pill(
                title = "FPS",
                isHorizontal = overlaySettings.isHorizontal,
            ) {
                if (overlaySettings.fps) {
                    Text(
                        text = "${data.FPS}",
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                if (overlaySettings.frametime) {
                    FrametimeGraph(data, overlaySettings.isHorizontal)
                    Text(
                        text = "${String.format("%02.01f", data.Frametime)} ms",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
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
                        color = OffWhite,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (overlaySettings.fps) {
                            Text(
                                text = "${data.FPS}",
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        if (overlaySettings.frametime) {
                            Text(
                                text = "${String.format("%02.01f", data.Frametime)} ms",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }

                FrametimeGraph(data,overlaySettings.isHorizontal)
            }
        }
    }
}

@Composable
private fun FrametimeGraph(data: Data, isHorizontal: Boolean) {
    val largestFrametime = remember { mutableFloatStateOf(0f) }
    val listSize = 30
    val frametimePoints = remember { mutableStateListOf<Float>() }

    val frametimePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Color.White
            strokeWidth = 1f
            blendMode = BlendMode.Plus
        }
    }

    LaunchedEffect(data) {
        if (data.Frametime > largestFrametime.floatValue) {
            largestFrametime.floatValue = data.Frametime
        }
        frametimePoints.add(data.Frametime / largestFrametime.floatValue)
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
            val colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Transparent)
            val frametimeZip = frametimePoints.zipWithNext()

            drawIntoCanvas { canvas ->
                frametimeZip.fastForEachIndexed { index, pair ->
                    val x0 = size.width * (1f / listSize * (index))
                    val y0 = (size.height * (1f - pair.first))
                    val x1 = size.width * (1f / listSize * (index + 1))
                    val y1 = (size.height * (1f - pair.second))

                    canvas.drawLine(Offset(x0, y0), Offset(x1, y1), frametimePaint)
                }
            }
            drawRect(brush = Brush.horizontalGradient(colors), blendMode = BlendMode.DstIn)
        })
}

