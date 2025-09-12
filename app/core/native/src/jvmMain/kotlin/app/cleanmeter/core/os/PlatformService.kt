package app.cleanmeter.core.os

import app.cleanmeter.core.os.win32.WindowsService
import java.awt.Component

actual object PlatformService  {
    actual fun changeWindowTransparency(w: Component, isTransparent: Boolean) {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> WindowsService.changeWindowTransparency(w, isTransparent)
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

    actual fun getForegroundProcessName(): String? {
        return when(getCurrentPlatform()) {
            Platform.WINDOWS -> WindowsService.getForegroundProcessName()
            Platform.MACOS -> {
                // TODO: Implement macOS window transparency
                println("macOS window transparency not yet implemented")
                null
            }
            Platform.LINUX -> {
                // TODO: Implement Linux window transparency
                println("Linux window transparency not yet implemented")
                null
            }
        }
    }
}
