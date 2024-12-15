package app.cleanmeter.core.designsystem

import androidx.compose.runtime.staticCompositionLocalOf
import app.cleanmeter.core.designsystem.Theme.Light

object Theme {

    val Light: ColorScheme = defaultColorScheme
    val Dark: ColorScheme = darkColorScheme
}

public val LocalColorScheme = staticCompositionLocalOf<ColorScheme> { Light }