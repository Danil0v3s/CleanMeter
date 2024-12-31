package app.cleanmeter.target.desktop.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import app.cleanmeter.target.desktop.data.PREFERENCE_START_MINIMIZED
import app.cleanmeter.target.desktop.data.PreferencesRepository
import com.github.kwhat.jnativehook.GlobalScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.GraphicsEnvironment
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import kotlin.math.min

@Composable
fun ApplicationScope.SettingsWindow(
    isDarkTheme: Boolean,
    getOverlayPosition: () -> IntOffset,
    onApplicationExit: () -> Unit,
) {
    val maximumWindowBounds = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.height }
    val minimumHeight = remember { min(800, maximumWindowBounds) }
    var isVisible by remember {
        mutableStateOf(!PreferencesRepository.getPreferenceBoolean(PREFERENCE_START_MINIMIZED, false))
    }
    val icon = painterResource("imgs/logo.png")
    val state = rememberWindowState().apply {
        size = DpSize(650.dp, minimumHeight.dp)
    }

    Window(
        state = state,
        onCloseRequest = { isVisible = false },
        icon = icon,
        visible = isVisible,
        title = "Clean Meter",
        resizable = true,
        undecorated = true,
        transparent = true,
    ) {
        LaunchedEffect(state) {
            snapshotFlow { state.size }
                .onEach {
                    var size = it
                    if (it.width != 650.dp) {
                        size = it.copy(width = 650.dp)
                    }
                    if (it.height < minimumHeight.dp || it.height > maximumWindowBounds.dp) {
                        size = it.copy(height = minimumHeight.dp)
                    }
                    state.size = size
                }
                .launchIn(this)
        }

        Settings(
            isDarkTheme = isDarkTheme,
            onCloseRequest = { isVisible = false },
            onMinimizeRequest = { state.isMinimized = true },
            getOverlayPosition = getOverlayPosition,
            onExitRequest = { exitApplication() }
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
                    onApplicationExit()
                    exitApplication()
                })
            }
        )
    }
}