package br.com.firstsoft.target.server.ui.settings

import PreferencesRepository
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.github.kwhat.jnativehook.GlobalScreen
import ui.PREFERENCE_START_MINIMIZED
import ui.app.OverlaySettings
import ui.app.Settings

@Composable
fun ApplicationScope.SettingsWindow(
    overlaySettings: OverlaySettings,
    onOverlaySettings: (OverlaySettings) -> Unit,
    getOverlayPosition: () -> IntOffset,
) {
    var isVisible by remember {
        mutableStateOf(
            PreferencesRepository.getPreferenceBooleanNullable(
                PREFERENCE_START_MINIMIZED
            )?.not() ?: true
        )
    }
    val icon = painterResource("imgs/logo.png")
    val state = rememberWindowState().apply {
        size = DpSize(650.dp, 650.dp)
    }

    Window(
        state = state,
        onCloseRequest = { isVisible = false },
        icon = icon,
        visible = isVisible,
        title = "Clean Meter",
        resizable = false,
        undecorated = true,
        transparent = true,
    ) {
        Settings(
            overlaySettings = overlaySettings,
            onOverlaySettings = onOverlaySettings,
            onCloseRequest = { isVisible = false },
            onMinimizeRequest = { state.isMinimized = true },
            getOverlayPosition = getOverlayPosition
        )
    }

    if (!isVisible) {
        Tray(
            icon = icon,
            onAction = { isVisible = true },
            menu = {
                Item("Quit", onClick = {
                    try {
                        GlobalScreen.unregisterNativeHook()
                    } catch (e: Exception) {
                    }
                    exitApplication()
                })
            }
        )
    }
}