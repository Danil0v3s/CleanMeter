package br.com.firstsoft.target.server

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.common.reporting.ApplicationParams
import br.com.firstsoft.core.os.ProcessManager
import br.com.firstsoft.target.server.ui.overlay.OverlayWindow
import br.com.firstsoft.target.server.ui.settings.SettingsWindow
import kotlinx.coroutines.channels.Channel

fun composeApp(callbackChannel: Channel<Unit>) = application {
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
        channel = callbackChannel,
        onPositionChanged = {
            if (!state.overlaySettings.isPositionLocked) {
                overlayPosition = it
            }
        },
    )

    SettingsWindow(
        getOverlayPosition = { overlayPosition },
        onApplicationExit = {
            if (!ApplicationParams.isAutostart) {
                ProcessManager.stop()
            }
        }
    )
}