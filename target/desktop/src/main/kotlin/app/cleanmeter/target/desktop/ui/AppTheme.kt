package app.cleanmeter.target.desktop.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.core.designsystem.Theme

@Composable
fun AppTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalColorScheme provides if (isDarkTheme) Theme.Dark else Theme.Light,
            LocalTypography provides Theme.Typography,
            LocalLayoutDirection provides LayoutDirection.Ltr
        ) {
            content()
        }
    }
}