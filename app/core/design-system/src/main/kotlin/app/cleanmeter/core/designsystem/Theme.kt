package app.cleanmeter.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import app.cleanmeter.core.designsystem.Theme.Light

object Theme {

    val Light: ColorScheme = defaultColorScheme
    val Dark: ColorScheme = darkColorScheme
    val Typography = Typography()
}

val LocalColorScheme = staticCompositionLocalOf { Light }
val LocalTypography = staticCompositionLocalOf { Theme.Typography }
