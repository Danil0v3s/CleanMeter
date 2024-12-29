package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalTypography

@Composable
fun ProgressUnit(unit: String) {
    Text(
        text = unit,
        style = LocalTypography.current.bodyM,
        color = Color.White,
        modifier = Modifier.padding(bottom = 1.dp)
    )
}

@Composable
fun ProgressLabel(label: String) {
    Text(
        text = label,
        style = LocalTypography.current.titleM,
        color = Color.White,
    )
}