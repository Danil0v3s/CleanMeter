package app.cleanmeter.target.desktop.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.AppTheme

@Composable
fun HotKeySymbol(
    keys: List<String>
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        keys.fastForEachIndexed { index, key ->
            KeySymbol(key)
            if (index != keys.lastIndex) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Trailing icon for exposed dropdown menu",
                    modifier = Modifier.height(10.dp),
                    tint = LocalColorScheme.current.icon.bolderActive
                )
            }
        }
    }
}

@Composable
private fun KeySymbol(key: String) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .background(LocalColorScheme.current.background.surfaceSunkenSubtle, RoundedCornerShape(10.dp))
            .padding(bottom = 3.dp)
            .background(LocalColorScheme.current.gradient.gradient1, RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp, vertical = 8.dp)
    ) {
        Text(
            text = key,
            style = LocalTypography.current.labelM,
            color = LocalColorScheme.current.text.inverse,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun PreviewHotKeySymbol() {
    Box(modifier = Modifier.size(200.dp)) {
        AppTheme(isDarkTheme = false) {
            HotKeySymbol(listOf("Ctrl", "Alt", "F10"))
        }
    }

}

@Composable
@Preview
private fun DarkPreviewHotKeySymbol() {
    Box(modifier = Modifier.size(200.dp)) {
        AppTheme(isDarkTheme = true) {
            HotKeySymbol(listOf("Ctrl", "Alt", "F10"))
        }
    }
}