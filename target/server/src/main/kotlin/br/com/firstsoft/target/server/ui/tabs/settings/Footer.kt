package ui

import Label
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FooterUi(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier.fillMaxWidth().height(32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "May your frames be high, and temps be low.",
            fontSize = 12.sp,
            color = AlmostVisibleGray,
            lineHeight = 0.sp,
            fontWeight = FontWeight(550),
            letterSpacing = 0.14.sp
        )

        TooltipArea({
            Label(text = "Check for updates")
        }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable {
                    uriHandler.openUri("https://github.com/Danil0v3s/CleanMeter/releases/latest")
                }) {
                Icon(
                    painter = painterResource("imgs/github.svg"),
                    null,
                    tint = AlmostVisibleGray,
                )

                Text(
                    text = "Version 0.0.3",
                    fontSize = 14.sp,
                    color = AlmostVisibleGray,
                    lineHeight = 0.sp,
                    fontWeight = FontWeight(550),
                    letterSpacing = 0.14.sp,
                )
            }
        }
    }
}
