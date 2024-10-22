package br.com.firstsoft.target.server

import PreferencesRepository
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import ui.PREFERENCE_START_MINIMIZED
import ui.app.Overlay
import ui.app.OverlaySettings
import ui.app.Settings
import win32.WindowsService
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

val positions = listOf(
    Alignment.TopStart,
    Alignment.TopCenter,
    Alignment.TopEnd,
    Alignment.BottomStart,
    Alignment.BottomCenter,
    Alignment.BottomEnd,
)

fun main() {
    val channel = Channel<Unit>()

    GlobalScreen.registerNativeHook()
    GlobalScreen.addNativeKeyListener(object : NativeKeyListener {
        override fun nativeKeyReleased(nativeEvent: NativeKeyEvent) {
            val isCtrl = nativeEvent.modifiers.and(NativeKeyEvent.CTRL_MASK) > 0
            val isAlt = nativeEvent.modifiers.and(NativeKeyEvent.VC_ALT) > 0
            val isF10 = nativeEvent.keyCode == NativeKeyEvent.VC_F10

            if (isCtrl && isAlt && isF10) {
                channel.trySend(Unit)
            }
        }
    })

    application {
        var overlaySettings by remember { mutableStateOf(OverlaySettings()) }

        OverlayWindow(channel, overlaySettings)

        SettingsWindow {
            overlaySettings = it
        }
    }
}

@Composable
private fun ApplicationScope.OverlayWindow(
    channel: Channel<Unit>,
    overlaySettings: OverlaySettings
) {
    var isVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        channel.receiveAsFlow().collectLatest {
            isVisible = !isVisible
        }
    }

    val alignment = remember(overlaySettings.positionIndex) { positions[overlaySettings.positionIndex] }
    val overlayState = rememberWindowState().apply {
        size = if (overlaySettings.isHorizontal) DpSize(1024.dp, 80.dp) else DpSize(350.dp, 1024.dp)
        placement = WindowPlacement.Floating
        position = WindowPosition.Aligned(alignment)
    }

    val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val screenDevices = graphicsEnvironment.screenDevices
    val graphicsConfiguration = screenDevices[overlaySettings.selectedDisplayIndex].defaultConfiguration
    val taskbarHeight = Toolkit.getDefaultToolkit().screenSize.height - graphicsEnvironment.maximumWindowBounds.height

    Window(
        state = overlayState,
        onCloseRequest = { exitApplication() },
        visible = isVisible,
        title = "Clean Meter",
        resizable = false,
        alwaysOnTop = true,
        transparent = true,
        undecorated = true,
        focusable = false,
        enabled = false,
    ) {
        LaunchedEffect(alignment, overlaySettings.selectedDisplayIndex) {
            val location = when (alignment) {
                Alignment.TopStart -> IntSize(graphicsConfiguration.bounds.x, graphicsConfiguration.bounds.y)
                Alignment.TopCenter -> IntSize(
                    graphicsConfiguration.bounds.x + (graphicsConfiguration.bounds.width / 2) - (window.bounds.width / 2),
                    graphicsConfiguration.bounds.y
                )

                Alignment.TopEnd -> IntSize(
                    graphicsConfiguration.bounds.x + graphicsConfiguration.bounds.width - window.bounds.width,
                    graphicsConfiguration.bounds.y
                )

                Alignment.BottomStart -> IntSize(
                    graphicsConfiguration.bounds.x,
                    graphicsConfiguration.bounds.y + graphicsConfiguration.bounds.height - window.bounds.height - taskbarHeight
                )

                Alignment.BottomCenter -> IntSize(
                    graphicsConfiguration.bounds.x + (graphicsConfiguration.bounds.width / 2) - (window.bounds.width / 2),
                    graphicsConfiguration.bounds.y + graphicsConfiguration.bounds.height - window.bounds.height - taskbarHeight
                )

                Alignment.BottomEnd -> IntSize(
                    graphicsConfiguration.bounds.x + graphicsConfiguration.bounds.width - window.bounds.width,
                    graphicsConfiguration.bounds.y + graphicsConfiguration.bounds.height - window.bounds.height - taskbarHeight
                )

                else -> IntSize.Zero
            }
            window.setLocation(location.width, location.height)
            window.toFront()
        }

        WindowsService.makeComponentTransparent(window)
        Overlay(overlaySettings = overlaySettings)
    }
}

@Composable
private fun ApplicationScope.SettingsWindow(
    onOverlaySettings: (OverlaySettings) -> Unit
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
        size = DpSize(650.dp, 900.dp)
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
        Settings(onOverlaySettings = onOverlaySettings)
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
