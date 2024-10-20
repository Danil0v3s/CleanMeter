package win32

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser
import java.awt.Component

class WindowsService {

    var lastError: Int = 0
        private set

    fun openMemoryMapFile(filename: String): WinNT.HANDLE? {
        val memMapFile = Kernel32Impl.KERNEL_32.OpenFileMapping(WinNT.SECTION_MAP_READ, false, filename)
        lastError = Kernel32Impl.KERNEL_32.GetLastError()

        return memMapFile
    }

    fun openEventFile(filename: String): WinNT.HANDLE? {
        val memMapFile = Kernel32Impl.KERNEL_32.OpenEvent(WinNT.SYNCHRONIZE, false, filename)
        lastError = Kernel32Impl.KERNEL_32.GetLastError()
        return memMapFile
    }

    fun waitForEvent(handle: HANDLE): Boolean {
        return Kernel32Impl.KERNEL_32.WaitForSingleObject(handle, 500) == WinBase.WAIT_OBJECT_0
    }

    fun closeHandle(handle: WinNT.HANDLE) {
        Kernel32Impl.KERNEL_32.CloseHandle(handle)
    }

    fun mapViewOfFile(handle: WinNT.HANDLE?): Pointer? {
        handle ?: return null

        return Kernel32.INSTANCE.MapViewOfFile(handle, WinNT.SECTION_MAP_READ, 0, 0, 0)
    }

    fun unmapViewOfFile(pointer: Pointer) {
        Kernel32Impl.KERNEL_32.UnmapViewOfFile(pointer)
        lastError = Kernel32Impl.INSTANCE.GetLastError()
    }

    companion object {
        fun makeComponentTransparent(w: Component) {
            val hwnd = HWND().apply { pointer = Native.getComponentPointer(w) }
            val wl =
                User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE) or WinUser.WS_EX_LAYERED or WinUser.WS_EX_TRANSPARENT
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl)
        }
    }
}
