package app.cleanmeter.target.desktop.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@Composable
internal fun SectionTitle(title: String) {
    Text(
        text = title,
        style = LocalTypography.current.labelMSemiBold.copy(
            letterSpacing = 1.sp,
        ),
        color = LocalColorScheme.current.text.paragraph1,
    )
}