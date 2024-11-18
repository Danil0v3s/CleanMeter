package br.com.firstsoft.target.server.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.ColorTokens.BarelyVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray

@Composable
internal fun SettingsTab(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: Painter,
    modifier: Modifier = Modifier,
) = Tab(
    selected = selected,
    onClick = onClick,
    selectedContentColor = DarkGray,
    unselectedContentColor = MutedGray,
    modifier = modifier
        .fillMaxHeight()
        .background(
            color = if (selected) DarkGray else Color.White,
            shape = RoundedCornerShape(50)
        )
        .border(2.dp, BarelyVisibleGray, RoundedCornerShape(50))
        .padding(horizontal = 16.dp),
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = icon,
            contentDescription = "logo",
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(if (selected) Color.White else MutedGray),
        )
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else MutedGray,
            fontSize = 16.sp,
        )
    }
}