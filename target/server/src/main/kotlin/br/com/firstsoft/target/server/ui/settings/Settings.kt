package br.com.firstsoft.target.server.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.common.hardwaremonitor.cpuReadings
import br.com.firstsoft.core.common.hardwaremonitor.gpuReadings
import br.com.firstsoft.core.common.hardwaremonitor.networkReadings
import br.com.firstsoft.target.server.ui.AppTheme
import br.com.firstsoft.target.server.ui.ColorTokens.BackgroundOffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.ClearGray
import br.com.firstsoft.target.server.ui.ColorTokens.DarkGray
import br.com.firstsoft.target.server.ui.ColorTokens.Green
import br.com.firstsoft.target.server.ui.components.SettingsTab
import br.com.firstsoft.target.server.ui.components.TopBar
import br.com.firstsoft.target.server.ui.settings.tabs.AppSettingsUi
import br.com.firstsoft.target.server.ui.settings.tabs.HelpSettingsUi
import br.com.firstsoft.target.server.ui.settings.tabs.OverlaySettingsUi
import br.com.firstsoft.target.server.ui.settings.tabs.style.StyleUi
import br.com.firstsoft.updater.AutoUpdater
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun WindowScope.Settings(
    viewModel: SettingsViewModel = viewModel(),
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    getOverlayPosition: () -> IntOffset
) = AppTheme {
    val settingsState by viewModel.state.collectAsState(SettingsState())
    val isUpdateAvailable by AutoUpdater.isUpdateAvailable.collectAsState()


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

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                TabRow(selectedTabIndex) {
                    selectedTabIndex = it
                }

                TabContent(
                    selectedTabIndex = selectedTabIndex,
                    settingsState = settingsState,
                    viewModel = viewModel,
                    getOverlayPosition = getOverlayPosition,
                )
            }

            if (isUpdateAvailable) {
                UpdateLabel()
            }
        }
    }
}

@Composable
private fun BoxScope.UpdateLabel() {
    val updateProgress by AutoUpdater.progress.collectAsState()
    val isUpdating by AutoUpdater.isUpdating.collectAsState()
    val isUpdateAvailable by AutoUpdater.isUpdateAvailable.collectAsState()
    var updateArchive by remember { mutableStateOf<File?>(null) }
    var hasFinishedDownloading by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(1000)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            with(density) { 40.dp.roundToPx() }
        },
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .background(DarkGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .align(Alignment.BottomCenter)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "An update is available",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 2.5.dp),
            )

            Button(
                onClick = {
                    if (hasFinishedDownloading && updateArchive != null) {
                        AutoUpdater.prepareForManualUpdate(updateArchive!!)
                    } else {
                        AutoUpdater.downloadUpdate {
                            updateArchive = it
                            hasFinishedDownloading = true
                        }
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.White,
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                when {
                    !isUpdating && isUpdateAvailable -> {
                        if (hasFinishedDownloading) {
                            Icon(painterResource("icons/update.svg"), "")
                            Text(
                                text = "Install update",
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight(600),
                                modifier = Modifier.padding(bottom = 2.5.dp, start = 6.dp),
                            )
                        } else {
                            Icon(painterResource("icons/download.svg"), "")
                            Text(
                                text = "Download update",
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight(600),
                                modifier = Modifier.padding(bottom = 2.5.dp, start = 6.dp),
                            )
                        }
                    }

                    isUpdating && updateProgress in 0f..<1f -> {
                        Box {
                            Icon(
                                painterResource("icons/download.svg"),
                                "",
                                modifier = Modifier.align(Alignment.Center).size(12.dp)
                            )
                            CircularProgressIndicator(
                                progress = updateProgress,
                                modifier = Modifier.size(24.dp).align(Alignment.Center),
                                color = Green,
                                backgroundColor = ClearGray,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    else -> Unit
                }

            }
        }
    }
}

@Composable
private fun TabContent(
    selectedTabIndex: Int,
    settingsState: SettingsState,
    viewModel: SettingsViewModel,
    getOverlayPosition: () -> IntOffset
) {
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

@Composable
private fun TabRow(selectedTabIndex: Int, onTabIndexChange: (Int) -> Unit) {
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
                onClick = { onTabIndexChange(0) },
                label = "Stats",
                icon = painterResource("icons/data_usage.svg"),
            )
            SettingsTab(
                selected = selectedTabIndex == 1,
                onClick = { onTabIndexChange(1) },
                label = "Style",
                icon = painterResource("icons/layers.svg"),
                modifier = Modifier.padding(start = 8.dp)
            )
            SettingsTab(
                selected = selectedTabIndex == 2,
                onClick = { onTabIndexChange(2) },
                label = "Settings",
                icon = painterResource("icons/settings.svg"),
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        SettingsTab(
            selected = selectedTabIndex == 3,
            onClick = { onTabIndexChange(3) },
            label = "",
            icon = painterResource("icons/help.svg"),
            modifier = Modifier.weight(0.1f).padding(start = 4.dp),
        )
    }
}
