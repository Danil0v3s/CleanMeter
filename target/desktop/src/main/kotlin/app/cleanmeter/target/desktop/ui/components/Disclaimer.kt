package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@Composable
internal fun Disclaimer(text: String) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
) {
    Icon(
        imageVector = Icons.Outlined.Info,
        contentDescription = "Trailing icon for exposed dropdown menu",
        tint = LocalColorScheme.current.icon.bolderActive,
        modifier = Modifier.size(16.dp)
    )
    Text(
        text = text,
        color = LocalColorScheme.current.text.disabled,
        style = LocalTypography.current.labelSMedium,
        modifier = Modifier.wrapContentHeight()
    )
}