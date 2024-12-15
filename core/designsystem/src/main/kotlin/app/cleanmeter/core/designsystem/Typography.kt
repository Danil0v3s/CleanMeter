package app.cleanmeter.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

class Typography {

    /**
     * fontSize = 16.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Medium,
     */
    val titleM: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Medium,
        )

    /**
     * fontSize = 16.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Medium,
     */
    val titleMMedium: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 16.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Medium,
        )

    /**
     * fontSize = 14.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Normal,
     */
    val labelL: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Normal,
        )

    /**
     * fontSize = 14.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Thin,
     */
    val labelLThin: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Thin,
        )

    /**
     * fontSize = 14.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Medium,
     */
    val labelLMedium: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Medium,
        )

    /**
     * fontSize = 14.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.SemiBold,
     */
    val labelLSemiBold: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 14.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.SemiBold,
        )

    /**
     * fontSize = 13.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Normal,
     */
    val labelM: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 13.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Normal,
        )

    /**
     * fontSize = 13.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.SemiBold,
     */
    val labelMSemiBold: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 13.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.SemiBold,
        )

    /**
     * fontSize = 12.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Normal,
     */
    val labelS: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 12.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Normal,
        )

    /**
     * fontSize = 10.sp,
     * lineHeight = 0.sp,
     * fontWeight = FontWeight.Normal,
     */
    val bodyM: TextStyle
        @Composable get() = defaultTextStyle.copy(
            fontSize = 10.sp,
            lineHeight = 0.sp,
            fontWeight = FontWeight.Normal,
        )

    private val fontFamily = FontFamily(
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

    private val defaultTextStyle: TextStyle
        @Composable get() = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            color = LocalColorScheme.current.text.paragraph1
        )
}