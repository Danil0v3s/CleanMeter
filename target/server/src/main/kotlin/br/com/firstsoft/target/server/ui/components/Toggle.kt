package br.com.firstsoft.target.server.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.Green
import br.com.firstsoft.target.server.ui.overlay.conditional

@Composable
fun Toggle(
    checked: Boolean,
    thumbContent: (@Composable () -> Unit)? = null,
    checkedTrackColor: Color = Green,
    customSize: Boolean = false,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) =
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = checkedTrackColor,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = AlmostVisibleGray,
            uncheckedBorderColor = Color.Transparent,
        ),
        modifier = modifier.conditional(
            predicate = customSize,
            ifTrue = { this },
            ifFalse = { scale(0.7f).height(20.dp) }),
        thumbContent = thumbContent
    )