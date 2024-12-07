package br.com.firstsoft.target.server.ui.overlay

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.os.win32.WindowsService
import br.com.firstsoft.target.server.ApplicationViewModelStoreOwner
import br.com.firstsoft.target.server.KeyboardEvent
import br.com.firstsoft.target.server.KeyboardManager
import kotlinx.coroutines.flow.collectLatest
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

val positions = listOf(
    Alignment.TopStart,
    Alignment.TopCenter,
    Alignment.TopEnd,
    Alignment.BottomStart,
    Alignment.BottomCenter,
    Alignment.BottomEnd,
)

@Composable
fun ApplicationScope.OverlayWindow(
    viewModel: OverlayViewModel = viewModel(ApplicationViewModelStoreOwner),
    onPositionChanged: (IntOffset) -> Unit
) {
    val overlayState by viewModel.state.collectAsState(OverlayState())

    if (overlayState.overlaySettings == null) return

    var isVisible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        KeyboardManager.filter(KeyboardEvent.ToggleOverlay).collectLatest {
            isVisible = !isVisible
        }
    }

    val overlayWindowState = rememberWindowState().apply {
        size = if (overlayState.overlaySettings!!.isHorizontal) DpSize(1280.dp, 80.dp) else DpSize(350.dp, 1280.dp)
        placement = WindowPlacement.Floating
    }

    val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val screenDevices = graphicsEnvironment.screenDevices
    val graphicsConfiguration = screenDevices[overlayState.overlaySettings!!.selectedDisplayIndex].defaultConfiguration
    val taskbarHeight = Toolkit.getDefaultToolkit().screenSize.height - graphicsEnvironment.maximumWindowBounds.height

    Window(
        state = overlayWindowState,
        onCloseRequest = { exitApplication() },
        visible = isVisible,
        title = "Clean Meter",
        resizable = false,
        alwaysOnTop = true,
        transparent = true,
        undecorated = true,
        focusable = !overlayState.overlaySettings!!.isPositionLocked,
        enabled = !overlayState.overlaySettings!!.isPositionLocked,
    ) {
        window.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(e: ComponentEvent) {
                onPositionChanged(IntOffset(e.component.x, e.component.y))
            }
        })

        LaunchedEffect(overlayState.overlaySettings) {
            if (overlayState.overlaySettings!!.positionIndex < 6) {
                val alignment = positions[overlayState.overlaySettings!!.positionIndex]
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

                overlayWindowState.position = WindowPosition.Aligned(alignment)
                window.setLocation(location.width, location.height)
            } else {
                overlayWindowState.position =
                    WindowPosition.Absolute(
                        overlayState.overlaySettings!!.positionX.dp,
                        overlayState.overlaySettings!!.positionY.dp
                    )
            }

            window.toFront()
        }

        WindowsService.changeWindowTransparency(window, overlayState.overlaySettings!!.isPositionLocked)

        WindowDraggableArea {
            Overlay(
                overlaySettings = overlayState.overlaySettings!!,
                data = overlayState.hardwareData
            )
        }
    }
}