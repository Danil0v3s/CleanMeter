package app.cleanmeter.target.desktop.ui.components.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toUpperCase
import app.cleanmeter.target.desktop.ui.components.SectionTitle
import app.cleanmeter.target.desktop.ui.components.Toggle

@Composable
fun ToggleSection(
    title: String,
    isEnabled: Boolean,
    onSwitchToggle: (Boolean) -> Unit,
    content: @Composable () -> Unit
) = SectionBody {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(title = title.uppercase())
        Toggle(
            checked = isEnabled,
            onCheckedChange = onSwitchToggle
        )
    }

    if (isEnabled) {
        content()
    }
}