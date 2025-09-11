package app.cleanmeter.core.os.win32

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import java.awt.Component

internal object WindowsService {
    fun changeWindowTransparency(w: Component, isTransparent: Boolean) {
        val hwnd = HWND().apply { pointer = Native.getComponentPointer(w) }
        val wl = if (isTransparent) {
            User32.INSTANCE.GetWindowLong(
                hwnd,
                WinUser.GWL_EXSTYLE
            ) or WinUser.WS_EX_LAYERED or WinUser.WS_EX_TRANSPARENT
        } else {
            User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE) or WinUser.WS_EX_LAYERED and WinUser.WS_EX_TRANSPARENT.inv()
        }
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl)
    }
}
