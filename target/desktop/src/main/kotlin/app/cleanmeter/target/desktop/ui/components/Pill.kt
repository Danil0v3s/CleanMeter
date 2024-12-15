package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.ColorTokens.OffWhite
import app.cleanmeter.target.desktop.ui.overlay.conditional

@Composable
fun Pill(
    title: String,
    isHorizontal: Boolean,
    minWidth: Dp = 80.dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .conditional(
                predicate = isHorizontal,
                ifTrue = {
                    fillMaxHeight().widthIn(min = minWidth).background(Color.Black.copy(alpha = 0.3f), CircleShape)
                },
                ifFalse = {
                    background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                },
            )
            .padding(vertical = if (isHorizontal) 4.dp else 8.dp, horizontal = 12.dp)
    ) {
        Text(
            text = title,
            style = LocalTypography.current.bodyM.copy(
                letterSpacing = 1.sp,
            ),
            color = OffWhite,
        )

        content()
    }
}