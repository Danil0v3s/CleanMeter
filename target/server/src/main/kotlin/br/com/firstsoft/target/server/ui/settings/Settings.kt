package br.com.firstsoft.target.server.ui.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.common.hwinfo.cpuReadings
import br.com.firstsoft.core.common.hwinfo.gpuReadings
import br.com.firstsoft.target.server.ui.AppTheme
import br.com.firstsoft.target.server.ui.ColorTokens.BackgroundOffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.BarelyVisibleGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.MutedGray
import br.com.firstsoft.target.server.ui.settings.tabs.AppSettingsUi
import br.com.firstsoft.target.server.ui.settings.tabs.OverlaySettingsUi
import br.com.firstsoft.target.server.ui.settings.tabs.style.StyleUi

@Composable
fun WindowScope.Settings(
    viewModel: SettingsViewModel = viewModel(),
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    getOverlayPosition: () -> IntOffset
) = AppTheme {

    val settingsState by viewModel.state.collectAsState(SettingsState())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOffWhite, RoundedCornerShape(12.dp))
    ) {
        WindowDraggableArea {
            TopBar(onCloseRequest = onCloseRequest, onMinimizeRequest = onMinimizeRequest)
        }

        var selectedTabIndex by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                backgroundColor = Color.Transparent,
                contentColor = DarkGray,
                edgePadding = 0.dp,
                indicator = { tabPositions -> },
                divider = {}
            ) {
                SettingsTab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    label = "Stats",
                    icon = painterResource("icons/data_usage.svg"),
                )
                SettingsTab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    label = "Style",
                    icon = painterResource("icons/layers.svg"),
                    modifier = Modifier.padding(start = 8.dp)
                )
                SettingsTab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    label = "Settings",
                    icon = painterResource("icons/settings.svg"),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            when (selectedTabIndex) {
                0 -> OverlaySettingsUi(
                    overlaySettings = settingsState.overlaySettings,
                    onSectionSwitchToggle = { sectionType, isEnabled ->
                        viewModel.onEvent(
                            SettingsEvent.SwitchToggle(
                                sectionType,
                                isEnabled
                            )
                        )
                    },
                    onOptionsToggle = {
                        viewModel.onEvent(SettingsEvent.OptionsToggle(it))
                    },
                    onCustomSensorSelect = { sensorType, sensorId ->
                        viewModel.onEvent(SettingsEvent.CustomSensorSelect(sensorType, sensorId))
                    },
                    onDisplaySelect = {
                        viewModel.onEvent(SettingsEvent.DisplaySelect(it))
                    },
                    getCpuSensorReadings = { settingsState.hwInfoData?.cpuReadings() ?: emptyList() },
                    getGpuSensorReadings = { settingsState.hwInfoData?.gpuReadings() ?: emptyList() }
                )

                1 -> StyleUi(
                    overlaySettings = settingsState.overlaySettings,
                    getOverlayPosition = getOverlayPosition,
                    onOverlayPositionIndex = { viewModel.onEvent(SettingsEvent.OverlayPositionIndexSelect(it)) },
                    onOverlayCustomPosition = { offset, isPositionLocked ->
                        viewModel.onEvent(
                            SettingsEvent.OverlayCustomPositionSelect(
                                offset,
                                isPositionLocked
                            )
                        )
                    },
                    onLayoutChange = { viewModel.onEvent(SettingsEvent.OverlayOrientationSelect(it)) },
                    onOpacityChange = { viewModel.onEvent(SettingsEvent.OverlayOpacityChange(it)) },
                    onGraphTypeChange = { viewModel.onEvent(SettingsEvent.OverlayGraphChange(it)) }
                )

                2 -> AppSettingsUi()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
) {
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
                painter = painterResource("imgs/favicon.ico"),
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
                colorFilter = ColorFilter.tint(MutedGray),
                modifier = Modifier.clickable { onMinimizeRequest() }
            )
            TooltipArea({
                Text(
                    text = "Closing will minimize to the Tray",
                    fontWeight = FontWeight.Medium,
                    color = DarkGray,
                    fontSize = 14.sp
                )
            }) {
                Image(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    colorFilter = ColorFilter.tint(MutedGray),
                    modifier = Modifier.clickable { onCloseRequest() }
                )
            }
        }
    }
}


