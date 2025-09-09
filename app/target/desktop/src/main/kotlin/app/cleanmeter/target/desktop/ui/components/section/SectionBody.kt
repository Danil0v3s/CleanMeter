package app.cleanmeter.target.desktop.ui.components.section

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme

@Composable
fun SectionBody(content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = Modifier
        .animateContentSize()
        .background(LocalColorScheme.current.background.surfaceRaised, RoundedCornerShape(12.dp))
        .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
) {
    content()
}