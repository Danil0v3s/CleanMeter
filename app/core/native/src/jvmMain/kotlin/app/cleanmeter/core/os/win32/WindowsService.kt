package app.cleanmeter.core.os.win32

import com.sun.jna.Native
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference
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
            User32.INSTANCE.GetWindowLong(
                hwnd,
                WinUser.GWL_EXSTYLE
            ) or WinUser.WS_EX_LAYERED and WinUser.WS_EX_TRANSPARENT.inv()
        }
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl)
    }

    fun getForegroundProcessName(): String? {
        val hwnd = User32.INSTANCE.GetForegroundWindow() ?: return null

        val pid = IntByReference()
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid)

        val hProcess: WinNT.HANDLE = Kernel32.INSTANCE.OpenProcess(
            Kernel32.PROCESS_QUERY_INFORMATION or Kernel32.PROCESS_VM_READ,
            false,
            pid.value
        ) ?: return ""

        val buffer = CharArray(4096)
        val bufferSize = IntByReference(buffer.size)
        val success = Kernel32.INSTANCE.QueryFullProcessImageName(hProcess, 0, buffer, bufferSize)


        // Clean up: close the opened process
        Kernel32.INSTANCE.CloseHandle(hProcess)

        return if (success) String(buffer, 0, bufferSize.value) else null
    }
}
