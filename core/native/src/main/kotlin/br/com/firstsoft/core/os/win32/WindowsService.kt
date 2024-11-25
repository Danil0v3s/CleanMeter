package br.com.firstsoft.core.os.win32

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
import java.io.File
import java.io.IOException
import java.nio.file.Files.createFile
import java.nio.file.Path
import kotlin.io.path.exists

class WindowsService {

    var lastError: Int = 0
        private set

    fun openMemoryMapFile(filename: String): WinNT.HANDLE? {
        val memMapFile = Kernel32Impl.INSTANCE.OpenFileMapping(WinNT.SECTION_MAP_READ, false, filename)
        lastError = Kernel32Impl.INSTANCE.GetLastError()

        return memMapFile
    }

    fun openEventFile(filename: String): WinNT.HANDLE? {
        val memMapFile = Kernel32Impl.INSTANCE.OpenEvent(WinNT.SYNCHRONIZE, false, filename)
        lastError = Kernel32Impl.INSTANCE.GetLastError()
        return memMapFile
    }

    fun waitForEvent(handle: HANDLE): Boolean {
        return Kernel32Impl.INSTANCE.WaitForSingleObject(handle, 500) == WinBase.WAIT_OBJECT_0
    }

    fun closeHandle(handle: WinNT.HANDLE) {
        Kernel32Impl.INSTANCE.CloseHandle(handle)
    }

    fun mapViewOfFile(handle: WinNT.HANDLE?): Pointer? {
        handle ?: return null

        return Kernel32.INSTANCE.MapViewOfFile(handle, WinNT.SECTION_MAP_READ, 0, 0, 0)
    }

    fun unmapViewOfFile(pointer: Pointer) {
        Kernel32Impl.INSTANCE.UnmapViewOfFile(pointer)
        lastError = Kernel32Impl.INSTANCE.GetLastError()
    }

    companion object {
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

        fun isProcessElevated(): Boolean {
            try {
                File.createTempFile("cleanmeter", ".lock", File("C:/")).deleteOnExit()
            } catch (ex: Exception) {
                return false
            }
            return true
        }

        fun checkLockFile(): Boolean {
            val currentPath = Path.of("").toAbsolutePath()
            val filePath = Path.of(currentPath.toString(), "cleanmeter.lock")
            if (filePath.exists()) {
                return true
            }

            createFile(filePath).toFile().deleteOnExit()

            return false
        }
    }
}
