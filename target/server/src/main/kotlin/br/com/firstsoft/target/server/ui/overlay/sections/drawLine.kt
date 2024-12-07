package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.util.fastForEachIndexed

internal fun ContentDrawScope.drawLine(
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
