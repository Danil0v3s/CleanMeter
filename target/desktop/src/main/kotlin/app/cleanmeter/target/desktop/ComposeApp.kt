package app.cleanmeter.target.desktop

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.os.ProcessManager
import app.cleanmeter.target.desktop.ui.overlay.OverlayWindow
import app.cleanmeter.target.desktop.ui.settings.SettingsWindow

fun composeApp() = application {
    val viewModel: MainViewModel = viewModel(ApplicationViewModelStoreOwner)

    val state by viewModel.state.collectAsState(MainState())

    var overlayPosition by remember {
        mutableStateOf(
            IntOffset(
                state.overlaySettings.positionX,
                state.overlaySettings.positionY
            )
        )
    }

    OverlayWindow(
        onPositionChanged = {
            if (!state.overlaySettings.isPositionLocked) {
                overlayPosition = it
            }
        },
    )

    SettingsWindow(
        isDarkTheme = state.overlaySettings.isDarkTheme,
        getOverlayPosition = { overlayPosition },
        onApplicationExit = {
            if (!ApplicationParams.isAutostart) {
                ProcessManager.stop()
            }
        }
    )
}