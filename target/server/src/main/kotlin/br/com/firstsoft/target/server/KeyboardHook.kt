package br.com.firstsoft.target.server

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow

sealed class KeyboardEvent {
    data object ToggleOverlay : KeyboardEvent()
}

internal object KeyboardManager {

    private val _channel = Channel<KeyboardEvent>()
    val events = _channel.receiveAsFlow()

    fun filter(event: KeyboardEvent) = events.filterIsInstance(event::class)

    internal fun registerKeyboardHook() {
        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(object : NativeKeyListener {
                override fun nativeKeyReleased(nativeEvent: NativeKeyEvent) {
                    val isCtrl = nativeEvent.modifiers.and(NativeKeyEvent.CTRL_MASK) > 0
                    val isAlt = nativeEvent.modifiers.and(NativeKeyEvent.VC_ALT) > 0
                    val isF10 = nativeEvent.keyCode == NativeKeyEvent.VC_F10

                    if (isCtrl && isAlt && isF10) {
                        _channel.trySend(KeyboardEvent.ToggleOverlay)
                    }
                }
            })
        } catch (e: Throwable) {
            System.err.println("Could not register keyboard hook")
            e.printStackTrace()
        }
    }
}

