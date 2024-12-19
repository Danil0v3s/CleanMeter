package app.cleanmeter.target.desktop.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.ColorTokens.BorderGray
import app.cleanmeter.target.desktop.ui.ColorTokens.DarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.LabelGray

@Composable
fun FooterUi(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Github(uriHandler)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Discord(uriHandler)
            Donate(uriHandler)
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val text = remember {
                buildAnnotatedString {
                    append("Built by ")
                    pushStringAnnotation("click", "https://github.com/Danil0v3s")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Danil0v3s")
                    }
                    pop()
                    append(" & designed by ")
                    pushStringAnnotation("click", "https://www.instagram.com/mars.designs")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Mars")
                    }
                    pop()
                }
            }

            ClickableText(
                text = text,
                style = LocalTypography.current.labelS.copy(
                    color = LocalColorScheme.current.text.disabled,
                    letterSpacing = 0.14.sp,
                ),
                onClick = { offset ->
                    text.getStringAnnotations("click", offset, offset).firstOrNull()?.let {
                        uriHandler.openUri(it.item)
                    }
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    uriHandler.openUri("https://github.com/Danil0v3s/CleanMeter/releases/latest")
                }) {

                Text(
                    text = "Version ${System.getProperty("jpackage.app-version")}",
                    style = LocalTypography.current.labelS.copy(
                        color = LocalColorScheme.current.text.disabled,
                        letterSpacing = 0.14.sp,
                    )
                )
            }
        }
    }
}

@Composable
private fun Github(uriHandler: UriHandler) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                uriHandler.openUri("https://github.com/Danil0v3s/CleanMeter/releases/latest")
            }
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(12.dp))
            .border(1.dp, LocalColorScheme.current.border.bold, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(painterResource("icons/github.png"), "", modifier = Modifier.size(32.dp))
            Text(
                text = "Check the latest build",
                color = LocalColorScheme.current.text.heading,
                style = LocalTypography.current.labelLMedium,
            )
        }
        Icon(Icons.Rounded.ChevronRight, "")
    }
}

@Composable
private fun RowScope.Donate(uriHandler: UriHandler) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                uriHandler.openUri("https://ko-fi.com/danil0v3s")
            }
            .weight(.5f)
            .background(Color.Transparent, RoundedCornerShape(12.dp))
            .border(1.dp, LocalColorScheme.current.border.bold, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(painterResource("icons/ko-fi.png"), "")
            Text(
                text = "Like the work? Support us!",
                color = LocalColorScheme.current.text.heading,
                style = LocalTypography.current.labelLMedium,
            )
        }
        Image(Icons.Rounded.ChevronRight, "")
    }
}

@Composable
private fun RowScope.Discord(uriHandler: UriHandler) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                uriHandler.openUri("https://discord.gg/phqwe89cvE")
            }
            .weight(.5f)
            .background(Color.Transparent, RoundedCornerShape(12.dp))
            .border(1.dp, LocalColorScheme.current.border.bold, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(painterResource("icons/discord.png"), "")
            Text(
                text = "Join the discord server!",
                color = LocalColorScheme.current.text.heading,
                style = LocalTypography.current.labelLMedium,
            )
        }
        Image(Icons.Rounded.ChevronRight, "")
    }
}
