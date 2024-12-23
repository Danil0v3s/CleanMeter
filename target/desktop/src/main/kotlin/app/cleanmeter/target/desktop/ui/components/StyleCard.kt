package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.overlay.conditional

@Composable
internal fun StyleCard(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    customLabel: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) = Column(
    modifier = modifier
        .background(LocalColorScheme.current.background.surfaceRaised, RoundedCornerShape(8.dp))
        .conditional(
            predicate = isSelected,
            ifTrue = { border(2.dp, LocalColorScheme.current.border.brand, RoundedCornerShape(8.dp)) },
            ifFalse = { border(1.dp, LocalColorScheme.current.border.bold, RoundedCornerShape(8.dp)) }
        )
        .padding(4.dp)
        .clickable { onClick() }
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .background(LocalColorScheme.current.background.surfaceSunkenSubtle, RoundedCornerShape(4.dp))
            .padding(8.dp),
        content = content,
        contentAlignment = Alignment.Center
    )

    Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp)) {
        if (customLabel != null) {
            customLabel()
        } else {
            Text(
                text = label,
                style = LocalTypography.current.labelLMedium.copy(
                    letterSpacing = 0.14.sp
                ),
                color = LocalColorScheme.current.text.heading,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }
    }
}