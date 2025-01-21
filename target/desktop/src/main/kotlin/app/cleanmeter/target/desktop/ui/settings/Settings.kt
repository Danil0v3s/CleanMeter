package app.cleanmeter.target.desktop.ui.settings

import FilledButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cleanmeter.core.common.hardwaremonitor.cpuReadings
import app.cleanmeter.core.common.hardwaremonitor.gpuReadings
import app.cleanmeter.core.common.hardwaremonitor.networkReadings
import app.cleanmeter.core.designsystem.LocalColorScheme
import app.cleanmeter.core.designsystem.LocalTypography
import app.cleanmeter.target.desktop.ui.AppTheme
import app.cleanmeter.target.desktop.ui.components.RuntimeToast
import app.cleanmeter.target.desktop.ui.components.SettingsTab
import app.cleanmeter.target.desktop.ui.components.TopBar
import app.cleanmeter.target.desktop.ui.components.UpdateToast
import app.cleanmeter.target.desktop.ui.settings.tabs.AppSettingsUi
import app.cleanmeter.target.desktop.ui.settings.tabs.HelpSettingsUi
import app.cleanmeter.target.desktop.ui.settings.tabs.stats.StatsUi
import app.cleanmeter.target.desktop.ui.settings.tabs.style.StyleUi
import app.cleanmeter.updater.AutoUpdater
import app.cleanmeter.updater.UpdateState

@Composable
fun WindowScope.Settings(
    isDarkTheme: Boolean,
    viewModel: SettingsViewModel = viewModel(),
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    getOverlayPosition: () -> IntOffset,
    onExitRequest: () -> Unit,
) = AppTheme(isDarkTheme) {
    val settingsState by viewModel.state.collectAsState(SettingsState())
    val updaterState by AutoUpdater.state.collectAsState()

    if (settingsState.overlaySettings == null) {
        return@AppTheme
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorScheme.current.background.surface, RoundedCornerShape(12.dp))
    ) {
        WindowDraggableArea {
            TopBar(onCloseRequest = onCloseRequest, onMinimizeRequest = onMinimizeRequest)
        }

        var selectedTabIndex by remember { mutableStateOf(0) }

        Box(modifier = Modifier.fillMaxSize()) {
            if (!settingsState.adminConsent) {
                AdminConsent(
                    isDarkTheme = isDarkTheme,
                    onDeny = onExitRequest,
                    onAllow = {
                        viewModel.onEvent(SettingsEvent.ConsentGiven)
                    }
                )
            } else {
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

                if (updaterState !is UpdateState.NotAvailable && settingsState.isRuntimeAvailable) {
                    UpdateToast()
                }
                if (!settingsState.isRuntimeAvailable) {
                    RuntimeToast()
                }
            }
        }
    }
}

@Composable
private fun BoxScope.AdminConsent(
    isDarkTheme: Boolean,
    onDeny: () -> Unit,
    onAllow: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp).align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .background(LocalColorScheme.current.background.surfaceRaised, RoundedCornerShape(12.dp))
                .padding(top = 24.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource("icons/onboarding_${if (isDarkTheme) "dark" else "light"}.png"),
                contentDescription = null
            )
        }

        Text(
            text = "Administrative Privileges",
            style = LocalTypography.current.titleXXL,
            color = LocalColorScheme.current.text.heading,
        )

        Text(
            text = "Thank you for choosing CleanMeter!\n\n" +
                    "To function properly, CleanMeter requires administrative permissions and access to your local network. This is necessary for our processes to communicate with each other using sockets.\n\n" +
                    "If you’re okay with this, please grant the permissions below.",
            textAlign = TextAlign.Center,
            style = LocalTypography.current.labelLMedium.copy(
                lineHeight = 20.sp,
            ),
            color = LocalColorScheme.current.text.paragraph1,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledButton(
                label = "Close app",
                containerColor = LocalColorScheme.current.background.surfaceRaised,
                textStyle = LocalTypography.current.titleMMedium,
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                onClick = onDeny
            )
            FilledButton(
                label = "Allow",
                containerColor = LocalColorScheme.current.background.brand,
                textColor = LocalColorScheme.current.text.inverse,
                textStyle = LocalTypography.current.titleMMedium,
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                onClick = onAllow
            )
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
        0 -> StatsUi(
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
            onFpsApplicationSelect = {
                viewModel.onEvent(SettingsEvent.FpsApplicationSelect(it))
            },
            getCpuSensorReadings = { settingsState.hardwareData?.cpuReadings() ?: emptyList() },
            getGpuSensorReadings = { settingsState.hardwareData?.gpuReadings() ?: emptyList() },
            getNetworkSensorReadings = { settingsState.hardwareData?.networkReadings() ?: emptyList() },
            getHardwareSensors = { settingsState.hardwareData?.Hardwares ?: emptyList() },
            getPresentMonApps = { settingsState.hardwareData?.PresentMonApps ?: emptyList() },
            onBoundaryChange = { sensorType, boundaries ->
                viewModel.onEvent(SettingsEvent.BoundarySet(sensorType, boundaries))
            },
            getSensor = {
                when (it) {
                    SensorType.Framerate -> settingsState.overlaySettings.sensors.framerate
                    SensorType.Frametime -> settingsState.overlaySettings.sensors.frametime
                    SensorType.CpuTemp -> settingsState.overlaySettings.sensors.cpuTemp
                    SensorType.CpuUsage -> settingsState.overlaySettings.sensors.cpuUsage
                    SensorType.GpuTemp -> settingsState.overlaySettings.sensors.gpuTemp
                    SensorType.GpuUsage -> settingsState.overlaySettings.sensors.gpuUsage
                    SensorType.VramUsage -> settingsState.overlaySettings.sensors.vramUsage
                    SensorType.TotalVramUsed -> settingsState.overlaySettings.sensors.totalVramUsed
                    SensorType.RamUsage -> settingsState.overlaySettings.sensors.ramUsage
                    SensorType.UpRate -> settingsState.overlaySettings.sensors.upRate
                    SensorType.DownRate -> settingsState.overlaySettings.sensors.downRate
                    SensorType.NetGraph -> settingsState.overlaySettings.sensors.upRate // no sensor for netgraph
                }
            }
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

        2 -> AppSettingsUi(overlaySettings = settingsState.overlaySettings!!, onEvent = viewModel::onEvent)
        3 -> HelpSettingsUi(overlaySettings = settingsState.overlaySettings!!, onEvent = viewModel::onEvent, logSink = settingsState.logSink)
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
            contentColor = LocalColorScheme.current.background.brand,
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
            modifier = Modifier.weight(0.1f),
        )
    }
}
