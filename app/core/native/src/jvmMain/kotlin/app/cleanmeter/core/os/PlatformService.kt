package app.cleanmeter.core.os

import app.cleanmeter.core.os.util.isDev
import app.cleanmeter.core.os.win32.Shell32Impl
import app.cleanmeter.core.os.win32.Kernel32Impl
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
import java.nio.file.Path
import java.util.*
import kotlin.system.exitProcess

actual class PlatformService actual constructor() {
    actual companion object {
        actual fun changeWindowTransparency(w: Component, isTransparent: Boolean) {
            when (getCurrentPlatform()) {
                Platform.WINDOWS -> {
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
                Platform.MACOS -> {
                    // TODO: Implement macOS window transparency
                    println("macOS window transparency not yet implemented")
                }
                Platform.LINUX -> {
                    // TODO: Implement Linux window transparency
                    println("Linux window transparency not yet implemented")
                }
            }
        }

        actual fun isProcessElevated(): Boolean {
            return when (getCurrentPlatform()) {
                Platform.WINDOWS -> {
                    try {
                        File.createTempFile("cleanmeter", ".lock", File("C:/")).delete()
                        true
                    } catch (ex: Exception) {
                        false
                    }
                }
                Platform.MACOS -> {
                    // TODO: Implement macOS process elevation check
                    println("macOS process elevation check not yet implemented")
                    false
                }
                Platform.LINUX -> {
                    // TODO: Implement Linux process elevation check
                    println("Linux process elevation check not yet implemented")
                    false
                }
            }
        }

        actual fun tryElevateProcess(isAutostart: Boolean) {
            if (isAutostart) return
            when (getCurrentPlatform()) {
                Platform.WINDOWS -> {
                    if (!isDev() && !isProcessElevated()) {
                        elevateProcess()
                    }
                }
                Platform.MACOS -> {
                    // TODO: Implement macOS process elevation
                    println("macOS process elevation not yet implemented")
                }
                Platform.LINUX -> {
                    // TODO: Implement Linux process elevation
                    println("Linux process elevation not yet implemented")
                }
            }
        }

        actual fun elevateProcess() {
            when (getCurrentPlatform()) {
                Platform.WINDOWS -> {
                    val currentDir = Path.of("").toAbsolutePath().toString()
                    Shell32Impl.INSTANCE.ShellExecuteW(null, "runas", "$currentDir\\cleanmeter.exe", "", "", 10)
                    exitProcess(0)
                }
                Platform.MACOS -> {
                    // TODO: Implement macOS process elevation
                    println("macOS process elevation not yet implemented")
                }
                Platform.LINUX -> {
                    // TODO: Implement Linux process elevation
                    println("Linux process elevation not yet implemented")
                }
            }
        }
    }
}

private fun getCurrentPlatform(): Platform {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        "win" in osName -> Platform.WINDOWS
        "mac" in osName -> Platform.MACOS
        else -> Platform.LINUX
    }
}
