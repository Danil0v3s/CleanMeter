package app.cleanmeter.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorScheme(
    val background: Background,
    val text: Text,
    val border: Border,
    val icon: Icon,
) {
    @Immutable
    data class Background(
        val surface: Color,
        val surfaceRaised: Color,
        val surfaceOverlay: Color,
        val surfaceSunken: Color,
        val surfaceSunkenSubtle: Color,

        val brand: Color,
        val brandSubtle: Color,
        val brandHover: Color,
        val brandActive: Color,

        val success: Color,
        val successSubtle: Color,
        val successHover: Color,
        val successActive: Color,

        val warning: Color,
        val warningSubtle: Color,
        val warningHover: Color,
        val warningActive: Color,

        val danger: Color,
        val dangerSubtle: Color,
        val dangerHover: Color,
        val dangerActive: Color,
    )

    @Immutable
    data class Text(
        val heading: Color,
        val subHeading: Color,

        val paragraph1: Color,
        val paragraph2: Color,

        val disabled: Color,
        val disabledLighter: Color,

        val inverse: Color,
        val inverseSubtler: Color,

        val success: Color,
        val danger: Color,
        val warning: Color,
    )

    @Immutable
    data class Border(
        val subtler: Color,
        val inverse: Color,
        val subtle: Color,
        val bold: Color,
        val bolder: Color,

        val brand: Color,
        val brandSubtle: Color,

        val success: Color,
        val successDarker: Color,
        val danger: Color,
        val warning: Color,
    )

    @Immutable
    data class Icon(
        val bolder: Color,
        val bolderHover: Color,
        val bolderActive: Color,
        val bold: Color,
        val boldHover: Color,
        val boldActive: Color,

        val subtler: Color,
        val subtlerHover: Color,
        val subtlerActive: Color,
        val subtle: Color,
        val subtleHover: Color,
        val subtleActive: Color,

        val disabled: Color,
        val inverse: Color,
        val inverseHover: Color,
        val inverseActive: Color,

        val success: Color,
        val danger: Color,
        val warning: Color,
    )
}

internal val defaultColorScheme = ColorScheme(
    background = ColorScheme.Background(
        surface = Primitives.Gray.Gray100,
        surfaceRaised = Primitives.Plain.PlainWhite,
        surfaceOverlay = Primitives.Plain.PlainWhite,
        surfaceSunken = Primitives.Gray.Gray300,
        surfaceSunkenSubtle = Primitives.Gray.Gray50,
        brand = Primitives.Gray.Gray950,
        brandSubtle = Primitives.Gray.Gray900,
        brandHover = Primitives.Gray.Gray500,
        brandActive = Primitives.Gray.Gray600,
        success = Primitives.Green.Green700,
        successSubtle = Primitives.Green.Green50,
        successHover = Primitives.Green.Green500,
        successActive = Primitives.Green.Green600,
        warning = Primitives.Yellow.Yellow700,
        warningSubtle = Primitives.Yellow.Yellow50,
        warningHover = Primitives.Yellow.Yellow300,
        warningActive = Primitives.Yellow.Yellow400,
        danger = Primitives.Red.Red700,
        dangerSubtle = Primitives.Red.Red50,
        dangerHover = Primitives.Red.Red500,
        dangerActive = Primitives.Red.Red600,
    ),
    text = ColorScheme.Text(
        heading = Primitives.Gray.Gray950,
        subHeading = Primitives.Gray.Gray800,
        paragraph1 = Primitives.Gray.Gray600,
        paragraph2 = Primitives.Gray.Gray500,
        disabled = Primitives.Gray.Gray400,
        disabledLighter = Primitives.Gray.Gray300,
        inverse = Primitives.Plain.PlainWhite,
        inverseSubtler = Primitives.Gray.Gray50,
        success = Primitives.Green.Green900,
        danger = Primitives.Red.Red900,
        warning = Primitives.Yellow.Yellow900
    ),
    border = ColorScheme.Border(
        subtler = Primitives.Gray.Gray100,
        inverse = Primitives.Plain.PlainWhite,
        subtle = Primitives.Gray.Gray200,
        bold = Primitives.Gray.Gray300.copy(alpha = 0.5f),
        bolder = Primitives.Gray.Gray300,
        brand = Primitives.Gray.Gray950,
        brandSubtle = Primitives.Gray.Gray950.copy(alpha = 0.1f),
        success = Primitives.Green.Green600,
        successDarker = Primitives.Green.Green700,
        danger = Primitives.Red.Red600,
        warning = Primitives.Yellow.Yellow600,
    ),
    icon = ColorScheme.Icon(
        bolder = Primitives.Gray.Gray950,
        bolderHover = Primitives.Gray.Gray500,
        bolderActive = Primitives.Gray.Gray600,
        bold = Primitives.Gray.Gray800,
        boldHover = Primitives.Gray.Gray500,
        boldActive = Primitives.Gray.Gray600,
        subtler = Primitives.Gray.Gray600,
        subtlerHover = Primitives.Gray.Gray400,
        subtlerActive = Primitives.Gray.Gray500,
        subtle = Primitives.Gray.Gray500,
        subtleHover = Primitives.Gray.Gray300,
        subtleActive = Primitives.Gray.Gray400,
        disabled = Primitives.Gray.Gray400,
        inverse = Primitives.Plain.PlainWhite,
        inverseHover = Primitives.Gray.Gray50,
        inverseActive = Primitives.Gray.Gray200,
        success = Primitives.Green.Green500,
        danger = Primitives.Red.Red500,
        warning = Primitives.Yellow.Yellow300,
    )
)

internal val darkColorScheme = defaultColorScheme.copy(
    background = defaultColorScheme.background.copy(
        surface = Primitives.Gray.Gray800
    ),
)