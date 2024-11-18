package br.com.firstsoft.target.server.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.BarelyVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TopBar(
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .drawBehind {
                val y = size.height - 2.dp.toPx() / 2
                drawLine(
                    color = BarelyVisibleGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.dp.toPx()
                )
            }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource("imgs/favicon.ico"),
                contentDescription = "logo",
                modifier = Modifier.size(25.dp),
            )
            Text(
                text = "Clean Meter",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Rounded.Minimize,
                contentDescription = "Minimize",
                colorFilter = ColorFilter.tint(MutedGray),
                modifier = Modifier.clickable { onMinimizeRequest() }
            )
            TooltipArea({
                Text(
                    text = "Closing will minimize to the Tray",
                    fontWeight = FontWeight.Medium,
                    color = DarkGray,
                    fontSize = 14.sp
                )
            }) {
                Image(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    colorFilter = ColorFilter.tint(MutedGray),
                    modifier = Modifier.clickable { onCloseRequest() }
                )
            }
        }
    }
}