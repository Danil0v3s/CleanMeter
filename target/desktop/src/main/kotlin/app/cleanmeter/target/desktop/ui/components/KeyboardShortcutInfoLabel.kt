package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@Composable
internal fun KeyboardShortcutInfoLabel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(12.dp))
            .border(1.dp, LocalColorScheme.current.border.bold, RoundedCornerShape(12.dp))
            .padding(vertical = 22.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource("icons/info.svg"),
                contentDescription = "",
                tint = LocalColorScheme.current.icon.bolderActive
            )
            Text(
                text = "Hot key for showing/hiding the overlay",
                color = LocalColorScheme.current.text.heading,
                style = LocalTypography.current.labelLMedium,
                modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically),
            )
        }
        Image(
            painter = painterResource("icons/hotkey.png"),
            contentDescription = "",
            modifier = Modifier.height(32.dp)
        )
    }
}