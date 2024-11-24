package br.com.firstsoft.target.server.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.common.hardwaremonitor.cpuReadings
import br.com.firstsoft.core.common.hardwaremonitor.gpuReadings
import br.com.firstsoft.core.common.hardwaremonitor.networkReadings
import br.com.firstsoft.target.server.ui.AppTheme
import br.com.firstsoft.target.server.ui.ColorTokens.BackgroundOffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.components.SettingsTab
import br.com.firstsoft.target.server.ui.components.TopBar
import br.com.firstsoft.target.server.ui.settings.tabs.AppSettingsUi
import br.com.firstsoft.target.server.ui.settings.tabs.HelpSettingsUi
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

    if (settingsState.overlaySettings == null) {
        return@AppTheme
    }

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
            Row(modifier = Modifier.fillMaxWidth().height(44.dp)) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.weight(1f).height(44.dp),
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

                SettingsTab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    label = "",
                    icon = painterResource("icons/help.svg"),
                    modifier = Modifier.weight(0.1f).padding(start = 4.dp),
                )
            }

            when (selectedTabIndex) {
                0 -> OverlaySettingsUi(
                    overlaySettings = settingsState.overlaySettings!!,
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
                    getCpuSensorReadings = { settingsState.hardwareData?.cpuReadings() ?: emptyList() },
                    getGpuSensorReadings = { settingsState.hardwareData?.gpuReadings() ?: emptyList() },
                    getNetworkSensorReadings = { settingsState.hardwareData?.networkReadings() ?: emptyList() },
                    getHardwareSensors = { settingsState.hardwareData?.Hardwares ?: emptyList() },
                )

                1 -> StyleUi(
                    overlaySettings = settingsState.overlaySettings!!,
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
                    onGraphTypeChange = { viewModel.onEvent(SettingsEvent.OverlayGraphChange(it)) },
                    onOverlayCustomPositionEnable = { viewModel.onEvent(SettingsEvent.OverlayCustomPositionEnable(it)) },
                )

                2 -> AppSettingsUi()
                3 -> HelpSettingsUi()
                else -> Unit
            }
        }
    }
}
