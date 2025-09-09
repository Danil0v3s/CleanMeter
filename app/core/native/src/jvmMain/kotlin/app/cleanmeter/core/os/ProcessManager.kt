package app.cleanmeter.core.os

import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorProcessManager
import java.util.*

actual object ProcessManager {

    actual fun start() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> HardwareMonitorProcessManager.start()
            Platform.MACOS -> {
                // TODO: Implement macOS-specific process management
                println("macOS process management not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux-specific process management
                println("Linux process management not yet implemented")
            }
        }
    }

    actual fun stop() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> HardwareMonitorProcessManager.stop()
            Platform.MACOS -> {
                // TODO: Implement macOS-specific process management
                println("macOS process management not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux-specific process management
                println("Linux process management not yet implemented")
            }
        }
    }
}

enum class Platform {
    WINDOWS, MACOS, LINUX
}

private fun getCurrentPlatform(): Platform {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        "win" in osName -> Platform.WINDOWS
        "mac" in osName -> Platform.MACOS
        else -> Platform.LINUX
    }
}