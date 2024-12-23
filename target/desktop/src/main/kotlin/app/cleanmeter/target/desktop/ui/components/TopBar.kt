package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TopBar(
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
) {
    val colorScheme = LocalColorScheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .drawBehind {
                val y = size.height - 2.dp.toPx() / 2
                drawLine(
                    color = colorScheme.border.bold,
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
                style = LocalTypography.current.titleMMedium,
                color = LocalColorScheme.current.text.heading,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onMinimizeRequest() }, modifier = Modifier.size(20.dp).clearAndSetSemantics { }) {
                Icon(
                    imageVector = Icons.Rounded.Minimize,
                    contentDescription = "Minimize window",
                    tint = LocalColorScheme.current.icon.bolderActive,
                )
            }

            TooltipArea(
                delayMillis = 0,
                tooltip = {
                    Text(
                        text = "Closing will minimize to the Tray",
                        style = LocalTypography.current.labelM,
                        color = LocalColorScheme.current.text.heading,
                    )
                }) {
                IconButton(onClick = { onCloseRequest() }, modifier = Modifier.size(20.dp).clearAndSetSemantics { }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close window to tray",
                        tint = LocalColorScheme.current.icon.bolderActive,
                    )
                }
            }
        }
    }
}