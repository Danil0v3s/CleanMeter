package ui.app

import Title
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.target.server.ui.AppTheme
import br.com.firstsoft.target.server.ui.ColorTokens.AlmostGray
import br.com.firstsoft.target.server.ui.ColorTokens.BackgroundOffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.BarelyVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray
import br.com.firstsoft.target.server.ui.tabs.settings.OverlaySettingsUi
import kotlinx.serialization.Serializable
import ui.AppSettingsUi

const val OVERLAY_SETTINGS_PREFERENCE_KEY = "OVERLAY_SETTINGS_PREFERENCE_KEY"

val positionsLabels = listOf(
    "Top Start",
    "Top Center",
    "Top End",
    "Bottom Start",
    "Bottom Center",
    "Bottom End",
)

@Composable
fun Settings(
    onOverlaySettings: (OverlaySettings) -> Unit,
) = AppTheme {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOffWhite, RoundedCornerShape(12.dp))
    ) {
        TopBar()

        var selectedTabIndex by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                backgroundColor = Color.Transparent,
                contentColor = DarkGray,
                indicator = { tabPositions -> },
                divider = {}
            ) {
                SettingsTab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    label = "Stats",
                    icon = Icons.Rounded.Minimize,
                )
                SettingsTab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    label = "Style",
                    icon = Icons.Rounded.Minimize,
                )
                SettingsTab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    label = "Settings",
                    icon = Icons.Rounded.Minimize,
                )
            }

            when (selectedTabIndex) {
                0 -> OverlaySettingsUi(onOverlaySettings)
                1 -> AppSettingsUi()
                else -> Unit
            }
        }
    }
}

@Composable
private fun SettingsTab(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
) = Tab(
    selected = selected,
    onClick = onClick,
    selectedContentColor = DarkGray,
    unselectedContentColor = MutedGray,
    modifier = Modifier
        .background(
            color = if (selected) DarkGray else Color.Transparent,
            shape = RoundedCornerShape(50)
        )
        .border(2.dp, BarelyVisibleGray, RoundedCornerShape(50)),
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            imageVector = icon,
            contentDescription = "logo",
            modifier = Modifier.size(25.dp),
            colorFilter = ColorFilter.tint(if (selected) Color.White else MutedGray),
        )
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else MutedGray,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .drawBehind {
                val y = size.height - 2.dp.toPx() / 2
                drawLine(
                    color = BarelyVisibleGray,
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
                painter = painterResource("imgs/logo.png"),
                contentDescription = "logo",
                modifier = Modifier.size(25.dp),
            )
            Text(
                text = "Clean Meter",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Rounded.Minimize,
                contentDescription = "Minimize",
                colorFilter = ColorFilter.tint(AlmostGray),
            )
            Image(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                colorFilter = ColorFilter.tint(AlmostGray),
            )
        }
    }
}

@Serializable
data class OverlaySettings(
    val isHorizontal: Boolean = true,
    val positionIndex: Int = 0,
    val selectedDisplayIndex: Int = 0,
    val fps: Boolean = true,
    val frametime: Boolean = false,
    val cpuTemp: Boolean = true,
    val cpuUsage: Boolean = true,
    val gpuTemp: Boolean = true,
    val gpuUsage: Boolean = true,
    val vramUsage: Boolean = false,
    val ramUsage: Boolean = false,
    val progressType: ProgressType = ProgressType.Circular,
) {
    @Serializable
    enum class ProgressType {
        Circular, Bar, None
    }
}
