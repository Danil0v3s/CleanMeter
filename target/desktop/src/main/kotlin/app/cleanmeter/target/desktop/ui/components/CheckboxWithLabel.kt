package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography

@Composable
fun CheckboxWithLabel(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    trailingItem: @Composable (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = LocalColorScheme.current.background.brand,
                uncheckedColor = LocalColorScheme.current.background.surfaceSunken,
                checkmarkColor = LocalColorScheme.current.background.surfaceRaised,
                disabledColor = LocalColorScheme.current.background.surfaceSunkenSubtle,
            ),
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            style = LocalTypography.current.labelLMedium.copy(
                letterSpacing = 0.14.sp,
            ),
            color = LocalColorScheme.current.text.heading,
        )

        trailingItem?.invoke()
    }
}