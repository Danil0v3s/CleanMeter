package br.com.firstsoft.target.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application
import br.com.firstsoft.target.server.ui.overlay.OverlayWindow
import br.com.firstsoft.target.server.ui.settings.SettingsWindow
import kotlinx.coroutines.channels.Channel
import br.com.firstsoft.core.common.reporting.setDefaultUncaughtExceptionHandler
import br.com.firstsoft.core.os.hwinfo.HwInfoProcessManager

fun main() {
    setDefaultUncaughtExceptionHandler()

    val channel = Channel<Unit>()
    registerKeyboardHook(channel)

    HwInfoProcessManager.start()

    application {
        var overlaySettings by remember { mutableStateOf(PreferencesRepository.loadOverlaySettings()) }
        var overlayPosition by remember {
            mutableStateOf(
                IntOffset(
                    overlaySettings.positionX,
                    overlaySettings.positionY
                )
            )
        }

        OverlayWindow(
            channel = channel,
            overlaySettings = overlaySettings,
            onPositionChanged = {
                if (!overlaySettings.isPositionLocked) {
                    overlayPosition = it
                }
            },
        )

        SettingsWindow(
            overlaySettings = overlaySettings,
            onOverlaySettings = {
                overlaySettings = it
            },
            getOverlayPosition = { overlayPosition },
            onApplicationExit = {
                HwInfoProcessManager.stop()
            }
        )
    }
}
