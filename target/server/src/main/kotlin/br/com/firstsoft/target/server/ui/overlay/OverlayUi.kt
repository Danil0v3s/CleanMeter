package br.com.firstsoft.target.server.ui.overlay

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import br.com.firstsoft.core.common.hwinfo.DlRate
import br.com.firstsoft.core.common.hwinfo.DlRateUnit
import br.com.firstsoft.core.common.hwinfo.FPS
import br.com.firstsoft.core.common.hwinfo.Frametime
import br.com.firstsoft.core.common.hwinfo.GpuTemp
import br.com.firstsoft.core.common.hwinfo.GpuTempUnit
import br.com.firstsoft.core.common.hwinfo.GpuUsage
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.common.hwinfo.RamUsage
import br.com.firstsoft.core.common.hwinfo.RamUsagePercent
import br.com.firstsoft.core.common.hwinfo.UpRate
import br.com.firstsoft.core.common.hwinfo.UpRateUnit
import br.com.firstsoft.core.common.hwinfo.VramUsage
import br.com.firstsoft.core.common.hwinfo.VramUsagePercent
import br.com.firstsoft.core.common.hwinfo.getReading
import br.com.firstsoft.core.os.hwinfo.HwInfoReader
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.ColorTokens.Cyan
import br.com.firstsoft.target.server.ui.ColorTokens.OffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.Purple
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import br.com.firstsoft.target.server.ui.components.ProgressLabel
import br.com.firstsoft.target.server.ui.components.ProgressUnit
import java.util.*

inline fun Modifier.conditional(
    predicate: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (predicate) ifTrue(this) else ifFalse(this)

@Composable
fun OverlayUi(
    data: HwInfoData?,
    overlaySettings: OverlaySettings,
) {

    if (data == null) {
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
fun Content(data: HwInfoData, overlaySettings: OverlaySettings) {
    if (overlaySettings.isHorizontal) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fps(overlaySettings, data)
            gpu(overlaySettings, data)
            cpu(overlaySettings, data)
            ram(overlaySettings, data)
            net(overlaySettings, data)
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
                    net(overlaySettings, data)
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
private fun net(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.sensors.upRate.isEnabled || overlaySettings.sensors.downRate.isEnabled) {
        if (overlaySettings.isHorizontal) {
            Pill(
                title = "NET",
                isHorizontal = true,
            ) {
                if (overlaySettings.sensors.downRate.isEnabled) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Icon(
                            painterResource("icons/arrow_down.svg"),
                            "",
                            tint = Cyan,
                            modifier = Modifier.padding(end = 4.dp, bottom = 3.dp).alpha(data.DlRate.coerceAtMost(1f))
                        )
                    }
                }

                if (overlaySettings.sensors.upRate.isEnabled) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Icon(
                            painterResource("icons/arrow_down.svg"),
                            "",
                            tint = Purple,
                            modifier = Modifier.padding(end = 4.dp, bottom = 3.dp).rotate(180f).alpha(data.UpRate.coerceAtMost(1f))
                        )
                    }
                }

                if (overlaySettings.netGraph) {
                    NetGraph(data = data, isHorizontal = false, overlaySettings = overlaySettings)
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
                        text = "NET",
                        fontSize = 10.sp,
                        color = OffWhite,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (overlaySettings.sensors.downRate.isEnabled) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Icon(
                                    painterResource("icons/arrow_down.svg"),
                                    "",
                                    tint = Cyan,
                                    modifier = Modifier.padding(end = 4.dp, bottom = 3.dp).alpha(data.DlRate.coerceAtMost(1f))
                                )
                            }
                        }

                        if (overlaySettings.sensors.upRate.isEnabled) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Icon(
                                    painterResource("icons/arrow_down.svg"),
                                    "",
                                    tint = Purple,
                                    modifier = Modifier.padding(end = 4.dp, bottom = 3.dp).rotate(180f).alpha(data.UpRate.coerceAtMost(1f))
                                )
                            }
                        }
                    }
                }

                if (overlaySettings.netGraph) {
                    NetGraph(data = data, isHorizontal = false, overlaySettings = overlaySettings)
                }
            }
        }
    }
}

@Composable
private fun ram(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.sensors.ramUsage.isEnabled) {
        Pill(
            title = "RAM",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            Progress(
                value = data.RamUsagePercent / 100f,
                label = String.format("%02.1f", data.RamUsage / 1000, Locale.US),
                unit = "GB",
                progressType = overlaySettings.progressType
            )
        }
    }
}

@Composable
private fun cpu(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.sensors.cpuTemp.isEnabled || overlaySettings.sensors.cpuUsage.isEnabled) {
        Pill(
            title = "CPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.cpuTemp.isEnabled) {
                val cpuTemp = data.getReading(overlaySettings.sensors.cpuTemp.customReadingId)
                val cpuTempValue = (cpuTemp?.value ?: 1f).coerceAtLeast(1f).toInt()

                Progress(
                    value = cpuTempValue / 100f,
                    label = "$cpuTempValue",
                    unit = cpuTemp?.szUnit.orEmpty(),
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.sensors.cpuUsage.isEnabled) {
                val cpuUsage = data.getReading(overlaySettings.sensors.cpuUsage.customReadingId)
                val cpuUsageValue = (cpuUsage?.value ?: 1f).coerceAtLeast(1f)
                Progress(
                    value = cpuUsageValue / 100f,
                    label = String.format("%02d", cpuUsageValue.toInt(), Locale.US),
                    unit = cpuUsage?.szUnit.orEmpty(),
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}

@Composable
private fun gpu(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.sensors.gpuTemp.isEnabled || overlaySettings.sensors.gpuUsage.isEnabled || overlaySettings.sensors.vramUsage.isEnabled) {
        Pill(
            title = "GPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.sensors.gpuTemp.isEnabled) {
                Progress(
                    value = data.GpuTemp / 100f,
                    label = "${data.GpuTemp}",
                    unit = data.GpuTempUnit,
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.sensors.gpuUsage.isEnabled) {
                Progress(
                    value = data.GpuUsage / 100f,
                    label = String.format("%02d", data.GpuUsage, Locale.US),
                    unit = "%",
                    progressType = overlaySettings.progressType
                )
            }
            if (overlaySettings.sensors.vramUsage.isEnabled) {
                Progress(
                    value = data.VramUsagePercent / 100f,
                    label = String.format("%02.1f", data.VramUsage / 1000, Locale.US),
                    unit = "GB",
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}

@Composable
private fun fps(overlaySettings: OverlaySettings, data: HwInfoData) {
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
                        if (overlaySettings.sensors.framerate.isEnabled) {
                            Text(
                                text = "${data.FPS}",
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        if (overlaySettings.sensors.frametime.isEnabled) {
                            Text(
                                text = "${String.format("%02.01f", data.Frametime, Locale.US)} ms",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
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
private fun FrametimeGraph(data: HwInfoData, isHorizontal: Boolean) {
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
            val colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Transparent)
            val frametimeZip = frametimePoints.zipWithNext()

            drawIntoCanvas { canvas ->
                drawLine(frametimeZip, listSize, canvas, frametimePaint)
            }

            drawRect(brush = Brush.horizontalGradient(colors), blendMode = BlendMode.DstIn)
        })
}

@Composable
private fun NetGraph(data: HwInfoData, isHorizontal: Boolean, overlaySettings: OverlaySettings) {
    if (!overlaySettings.sensors.upRate.isEnabled && !overlaySettings.sensors.downRate.isEnabled) return

    val largestUp = remember { mutableFloatStateOf(0f) }
    val largestDown = remember { mutableFloatStateOf(0f) }
    val listSize = 30
    val upRatePoints = remember { mutableStateListOf<Float>() }
    val downRatePoints = remember { mutableStateListOf<Float>() }

    val upRatePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Purple
            strokeWidth = 1f
            blendMode = BlendMode.DstAtop
        }
    }
    val downRatePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Cyan
            strokeWidth = 1f
            blendMode = BlendMode.DstAtop
        }
    }

    LaunchedEffect(data) {
        upRatePoints.add((data.UpRate / largestUp.floatValue).coerceIn(0f, 1f))
        downRatePoints.add((data.DlRate / largestDown.floatValue + .2f).coerceIn(0f, 1f))
        if (upRatePoints.size > listSize) upRatePoints.removeFirst()
        if (downRatePoints.size > listSize) downRatePoints.removeFirst()
        largestUp.floatValue = upRatePoints.max()
        largestDown.floatValue = downRatePoints.max() + .2f
    }

    Box(modifier = Modifier
        .conditional(
            predicate = isHorizontal,
            ifTrue = { width(100.dp) },
            ifFalse = { fillMaxWidth() },
        )
        .height(if (isHorizontal) 45.dp else 30.dp)
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            val colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Transparent)
            val upRateZip = upRatePoints.zipWithNext()
            val downRateZip = downRatePoints.zipWithNext()

            drawIntoCanvas { canvas ->
                if (overlaySettings.sensors.upRate.isEnabled) {
                    drawLine(upRateZip, listSize, canvas, upRatePaint)
                }
                if (overlaySettings.sensors.downRate.isEnabled) {
                    drawLine(downRateZip, listSize, canvas, downRatePaint)
                }
            }
            drawRect(brush = Brush.horizontalGradient(colors), blendMode = BlendMode.DstIn)
        })
}

private fun ContentDrawScope.drawLine(
    pairs: List<Pair<Float, Float>>,
    listSize: Int,
    canvas: Canvas,
    paint: Paint
) {
    pairs.fastForEachIndexed { index, pair ->
        val x0 = size.width * (1f / listSize * (index))
        val y0 = (size.height * (1f - pair.first))
        val x1 = size.width * (1f / listSize * (index + 1))
        val y1 = (size.height * (1f - pair.second))

        canvas.drawLine(Offset(x0, y0), Offset(x1, y1), paint)
    }
}

