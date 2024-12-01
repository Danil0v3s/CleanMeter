package br.com.firstsoft.target.server

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.common.reporting.setDefaultUncaughtExceptionHandler
import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorProcessManager
import br.com.firstsoft.core.os.util.isDev
import br.com.firstsoft.core.os.win32.Shell32Impl
import br.com.firstsoft.core.os.win32.WindowsService
import br.com.firstsoft.target.server.data.OverlaySettingsRepository
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.overlay.OverlayWindow
import br.com.firstsoft.target.server.ui.settings.SettingsWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.nio.file.Path
import kotlin.system.exitProcess

data class MainState(
    val overlaySettings: OverlaySettings = OverlaySettings(),
)

class MainViewModel : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state: Flow<MainState>
        get() = _state

    init {
        observeOverlaySettings()
    }

    private fun observeOverlaySettings() {
        CoroutineScope(Dispatchers.IO).launch {
            OverlaySettingsRepository
                .data
                .collectLatest { overlaySettings ->
                    _state.update { it.copy(overlaySettings = overlaySettings) }
                }
        }
    }
}

object ApplicationViewModelStoreOwner : ViewModelStoreOwner {
    private val _store = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = _store

}

fun main(vararg args: String) {
    if(isAppAlreadyRunning()) {
        exitProcess(0)
    }

    setDefaultUncaughtExceptionHandler()

    val channel = Channel<Unit>()
    val isAutostart = isAutostart(args)

    tryElevateProcess(isAutostart)

    if (isDev()) {
        Runtime.getRuntime().addShutdownHook(Thread {
            HardwareMonitorProcessManager.stop()
        })
    } else {
        registerKeyboardHook(channel)
    }

    if (!isAutostart) {
        HardwareMonitorProcessManager.start()
    }

    application {
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
            channel = channel,
            onPositionChanged = {
                if (!state.overlaySettings.isPositionLocked) {
                    overlayPosition = it
                }
            },
        )

        SettingsWindow(
            getOverlayPosition = { overlayPosition },
            onApplicationExit = {
                if (!isAutostart) {
                    HardwareMonitorProcessManager.stop()
                }
            }
        )
    }
}

private fun isAutostart(args: Array<out String>) = (args.isNotEmpty() && args[0] == "--autostart")

private fun tryElevateProcess(isAutostart: Boolean) {
    if (isAutostart) return
    if (!isDev() && !WindowsService.isProcessElevated()) {
        val currentDir = Path.of("").toAbsolutePath().toString()
        Shell32Impl.INSTANCE.ShellExecuteW(null, "runas", "$currentDir\\cleanmeter.exe", "", "", 10)
        exitProcess(0)
    }
}

private fun isAppAlreadyRunning() = try {
    ServerSocket(1337).apply {
        Runtime.getRuntime().addShutdownHook(Thread {
            close()
        })
        CoroutineScope(Dispatchers.IO).launch {
            try {
                accept()
            } catch (_: Exception) {
                // consume the exception of accept since we do not really care if the socket was shutdown
            }
        }
    }
    false
} catch (ex: Exception) {
    true
}
