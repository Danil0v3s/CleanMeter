package app.cleanmeter.target.desktop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.ColorTokens.BarelyVisibleGray
import app.cleanmeter.target.desktop.ui.ColorTokens.DarkGray
import app.cleanmeter.target.desktop.ui.ColorTokens.MutedGray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun SettingsTab(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    // TODO: empty for now, figure a custom shape next
    val emptyInteractionSource = remember {
        object : MutableInteractionSource {
            override val interactions: Flow<Interaction>
                get() = emptyFlow()

            override suspend fun emit(interaction: Interaction) {
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return true
            }
        }
    }

    Tab(
        selected = selected,
        onClick = onClick,
        selectedContentColor = LocalColorScheme.current.background.brand,
        unselectedContentColor = LocalColorScheme.current.background.surfaceRaised,
        interactionSource = emptyInteractionSource,
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (selected) DarkGray else Color.White,
                shape = RoundedCornerShape(50)
            )
            .border(1.dp, LocalColorScheme.current.border.bold, RoundedCornerShape(50))
            .padding(horizontal = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = icon,
                contentDescription = "logo",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(if (selected) LocalColorScheme.current.icon.inverse else LocalColorScheme.current.icon.bolderActive),
            )
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    style = LocalTypography.current.titleM,
                    color = if (selected) LocalColorScheme.current.text.inverse else LocalColorScheme.current.text.paragraph1,
                )
            }
        }
    }
}