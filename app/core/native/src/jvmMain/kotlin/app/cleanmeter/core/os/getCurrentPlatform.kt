package app.cleanmeter.core.os

import java.util.Locale

enum class Platform {
    WINDOWS, MACOS, LINUX
}

fun getCurrentPlatform(): Platform {
    val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        "win" in osName -> Platform.WINDOWS
        "mac" in osName -> Platform.MACOS
        else -> Platform.LINUX
    }
}