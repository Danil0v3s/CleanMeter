package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.target.desktop.ui.overlay.conditional

@Composable
fun Toggle(
    checked: Boolean,
    thumbContent: (@Composable () -> Unit)? = null,
    customSize: Boolean = false,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = LocalColorScheme.current.background.surfaceOverlay,
        checkedTrackColor = LocalColorScheme.current.background.successHover,
        checkedBorderColor = Color.Transparent,
        uncheckedThumbColor = LocalColorScheme.current.background.surfaceOverlay,
        uncheckedTrackColor = LocalColorScheme.current.background.surfaceSunken,
        uncheckedBorderColor = Color.Transparent,
    ),
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) =
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = colors,
        modifier = modifier.conditional(
            predicate = customSize,
            ifTrue = { this },
            ifFalse = { scale(0.7f).height(20.dp) }),
        thumbContent = thumbContent
    )