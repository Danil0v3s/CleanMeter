package app.cleanmeter.target.desktop

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow

sealed class KeyboardEvent {
    data object ToggleOverlay : KeyboardEvent()
    data object ToggleRecording : KeyboardEvent()
}

internal object KeyboardManager {

    private val _channel = Channel<KeyboardEvent>(CONFLATED)
    val events = _channel.receiveAsFlow()

    fun filter(event: KeyboardEvent) = events.filterIsInstance(event::class)

    internal fun registerKeyboardHook() {
        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(object : NativeKeyListener {
                override fun nativeKeyReleased(nativeEvent: NativeKeyEvent) {
                    val isCtrl = nativeEvent.modifiers.and(NativeKeyEvent.CTRL_MASK) > 0
                    val isAlt = nativeEvent.modifiers.and(NativeKeyEvent.VC_ALT) > 0

                    if (!isCtrl && !isAlt) return

                    val event = when (nativeEvent.keyCode) {
                        NativeKeyEvent.VC_F10 -> KeyboardEvent.ToggleOverlay
                        NativeKeyEvent.VC_F11 -> KeyboardEvent.ToggleRecording
                        else -> null
                    }
                    event?.let { _channel.trySend(it) }
                }
            })
        } catch (e: Throwable) {
            System.err.println("Could not register keyboard hook")
            e.printStackTrace()
        }
    }
}

