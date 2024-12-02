package br.com.firstsoft.target.server

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kotlinx.coroutines.channels.Channel

internal fun registerKeyboardHook(channel: Channel<Unit>) {
    try {
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
    } catch (e: Throwable) {
        System.err.println("Could not register keyboard hook")
        e.printStackTrace()
    }
}