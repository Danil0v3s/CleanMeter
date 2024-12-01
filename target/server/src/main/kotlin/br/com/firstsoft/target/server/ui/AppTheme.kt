package br.com.firstsoft.target.server.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.LayoutDirection

val fontFamily = FontFamily(
    Font(resource = "font/inter_thin.ttf", weight = FontWeight.Thin),
    Font(resource = "font/inter_extralight.ttf", weight = FontWeight.ExtraLight),
    Font(resource = "font/inter_light.ttf", weight = FontWeight.Light),
    Font(resource = "font/inter_regular.ttf", weight = FontWeight.Normal),
    Font(resource = "font/inter_medium.ttf", weight = FontWeight.Medium),
    Font(resource = "font/inter_semibold.ttf", weight = FontWeight.SemiBold),
    Font(resource = "font/inter_bold.ttf", weight = FontWeight.Bold),
    Font(resource = "font/inter_extrabold.ttf", weight = FontWeight.ExtraBold),
    Font(resource = "font/inter_black.ttf", weight = FontWeight.Black),
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography(defaultFontFamily = fontFamily),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            content()
        }
    }
}