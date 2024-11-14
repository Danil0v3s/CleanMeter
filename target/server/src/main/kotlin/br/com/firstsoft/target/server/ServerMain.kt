package br.com.firstsoft.target.server

import PreferencesRepository
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
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
import com.sun.jna.platform.win32.WinDef.HWND
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import reporting.setDefaultUncaughtExceptionHandler
import ui.PREFERENCE_START_MINIMIZED
import ui.app.OVERLAY_SETTINGS_PREFERENCE_KEY
import ui.app.Overlay
import ui.app.OverlaySettings
import ui.app.Settings
import win32.Shell32Impl
import win32.WindowsService
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import java.nio.file.Path

val positions = listOf(
    Alignment.TopStart,
    Alignment.TopCenter,
    Alignment.TopEnd,
    Alignment.BottomStart,
    Alignment.BottomCenter,
    Alignment.BottomEnd,
)

private fun loadOverlaySettings(): OverlaySettings {
    val json = PreferencesRepository.getPreferenceString(OVERLAY_SETTINGS_PREFERENCE_KEY)
    val settings = if (json != null) {
        try {
            Json.decodeFromString<OverlaySettings>(json)
        } catch (e: Exception) {
            OverlaySettings()
        }
    } else {
        OverlaySettings()
    }
    return settings
}

private fun registerKeyboardHook(channel: Channel<Unit>) {
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
}

object HWInfo {
    fun start() {
        val hwnd = HWND()
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\app\\resources\\HWiNFO64.exe"
        Shell32Impl.INSTANCE.ShellExecuteW(hwnd, "runas", file, null, null, 0)
    }
}

fun main() {
    setDefaultUncaughtExceptionHandler()

    val channel = Channel<Unit>()
    registerKeyboardHook(channel)

    HWInfo.start()

    application {
        var overlaySettings by remember { mutableStateOf(loadOverlaySettings()) }
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
            getOverlayPosition = { overlayPosition }
        )
    }
}

@Composable
private fun ApplicationScope.OverlayWindow(
    channel: Channel<Unit>,
    overlaySettings: OverlaySettings,
    onPositionChanged: (IntOffset) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        channel.receiveAsFlow().collectLatest {
            isVisible = !isVisible
        }
    }

    val overlayState = rememberWindowState().apply {
        size = if (overlaySettings.isHorizontal) DpSize(1280.dp, 80.dp) else DpSize(350.dp, 1280.dp)
        placement = WindowPlacement.Floating
        position = WindowPosition.Absolute(overlaySettings.positionX.dp,overlaySettings.positionY.dp)
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
        focusable = !overlaySettings.isPositionLocked,
        enabled = !overlaySettings.isPositionLocked,
    ) {
        window.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(e: ComponentEvent) {
                onPositionChanged(IntOffset(e.component.x, e.component.y))
            }
        })

        LaunchedEffect(overlaySettings) {
            if (overlaySettings.positionIndex < 6) {
                val alignment = positions[overlaySettings.positionIndex]
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
                overlayState.position = WindowPosition.Aligned(alignment)
                window.setLocation(location.width, location.height)
            } else {
                overlayState.position = WindowPosition.Absolute(overlaySettings.positionX.dp,overlaySettings.positionY.dp)
            }

            window.toFront()
        }

        WindowsService.changeWindowTransparency(window, overlaySettings.isPositionLocked)

        WindowDraggableArea {
            Overlay(overlaySettings = overlaySettings)
        }
    }
}

@Composable
private fun ApplicationScope.SettingsWindow(
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
