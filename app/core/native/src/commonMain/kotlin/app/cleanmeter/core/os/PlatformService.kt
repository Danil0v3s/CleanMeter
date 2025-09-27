package app.cleanmeter.core.os

import java.awt.Component

/**
 * Platform-specific service interface for OS-level operations
 */
expect object PlatformService {
    fun changeWindowTransparency(w: Component, isTransparent: Boolean)
    fun getForegroundProcessName(): String?
}
