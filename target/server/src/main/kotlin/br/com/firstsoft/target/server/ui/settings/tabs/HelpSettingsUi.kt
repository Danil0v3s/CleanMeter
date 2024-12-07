package br.com.firstsoft.target.server.ui.settings.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.LabelGray
import br.com.firstsoft.target.server.ui.components.CollapsibleSection

@Composable
internal fun HelpSettingsUi() {
    Column(
        modifier = Modifier.padding(bottom = 8.dp, top = 20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CollapsibleSection(
            title = "HOW TO SETUP",
        ) {
            StyledNumberedList(
                "Run it",
                "Setup the sensors",
                "Enjoy"
            )
        }

        CollapsibleSection(
            title = "CURRENT LIMITATIONS",
        ) {
            BulletList(
                listOf("Doesn't work with exclusive fullscreen")
            )
        }

        CollapsibleSection(
            title = "FREQUENTLY ASKED QUESTIONS",
        ) {
            FrequentlyAskedQuestions(
                "The sensors look wrong" to buildAnnotatedString { append("Try setting up each sensor via the Stats tab") },
                "Neither sensors dropdown or the overlay are showing up" to buildAnnotatedString {
                    append("You need to have ")
                    pushStringAnnotation("click", "https://dotnet.microsoft.com/en-us/download/dotnet/thank-you/runtime-desktop-8.0.11-windows-x64-installer")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(".NET Core Framework")
                    }
                    pop()
                    append(" installed")
                },
                "Having problems like crashes or still nothing showing up?" to buildAnnotatedString {
                    append("Launch the app with ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("--verbose (eg: cleanmeter.exe --verbose)")
                    }
                    append(" params and ping us on our ")
                    pushStringAnnotation("click", "https://discord.gg/phqwe89cvE")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Discord Server")
                    }
                    pop()
                    append(" or ")
                    pushStringAnnotation("click", "https://github.com/Danil0v3s/CleanMeter/issues")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("GitHub Issues")
                    }
                    pop()
                },
                "Still has questions?" to buildAnnotatedString {
                    append("Join our ")
                    pushStringAnnotation("click", "https://discord.gg/phqwe89cvE")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Discord Server")
                    }
                    pop()
                }
            )
        }
    }
}

@Composable
private fun BulletList(
    items: List<String>,
) {
    val bullet = "\u2022"
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    Text(
        text = buildAnnotatedString {
            items.forEach {
                withStyle(style = paragraphStyle) {
                    append(bullet)
                    append("  ")
                    append(it)
                }
            }
        },
        style = TextStyle(
            fontSize = 14.sp,
            color = DarkGray,
            fontWeight = FontWeight(500)
        )
    )
}

@Composable
private fun StyledNumberedList(
    vararg items: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.background(DarkGray, CircleShape).size(26.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Thin,
                            lineHeight = 0.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.align(Alignment.Center).padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = item,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = DarkGray,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 0.sp,
                    )
                )
            }
        }
    }
}

@Composable
private fun FrequentlyAskedQuestions(
    vararg items: Pair<String, AnnotatedString>
) {
    val uriHandler = LocalUriHandler.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEachIndexed { index, pair ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("${index + 1}.")
                        append("  ")
                        append(pair.first)
                    },
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = DarkGray,
                        fontWeight = FontWeight(500),
                        lineHeight = 0.sp
                    )
                )
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    ClickableText(
                        text = pair.second,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = LabelGray,
                            fontWeight = FontWeight(400),
                            lineHeight = 0.sp
                        ),
                        onClick = { offset ->
                            pair.second.getStringAnnotations("click", offset, offset).firstOrNull()?.let {
                                uriHandler.openUri(it.item)
                            }
                        }
                    )
                }
            }
        }
    }
}