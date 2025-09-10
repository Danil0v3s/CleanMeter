package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.Platform
import app.cleanmeter.core.os.util.isDev
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.util.*

actual object HardwareMonitorProcessManager {
    private var process: Process? = null

    actual fun start() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                val currentDir = Path.of("").toAbsolutePath().toString()
                val file = if (isDev()) {
                    "$currentDir\\bin\\HardwareMonitor.exe"
                } else {
                    "$currentDir\\app\\resources\\HardwareMonitor.exe"
                }

                val process = ProcessBuilder().apply {
                    command("cmd.exe", "/c", file)
                }.start()

                val scannerIn = Scanner(process.inputStream)
                val scannerErr = Scanner(process.errorStream)

                CoroutineScope(Dispatchers.IO).launch {
                    while (scannerIn.hasNextLine()) {
                        System.out.println(scannerIn.nextLine())
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    while (scannerErr.hasNextLine()) {
                        System.err.println(scannerErr.nextLine())
                    }
                }

                this.process = process
            }
            Platform.MACOS -> {
                // TODO: Implement macOS hardware monitor
                println("macOS hardware monitor not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux hardware monitor
                println("Linux hardware monitor not yet implemented")
            }
        }
    }

    actual fun stop() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                process?.apply {
                    descendants().forEach(ProcessHandle::destroy)
                    destroy()
                }
                process = null
            }
            Platform.MACOS -> {
                // TODO: Implement macOS process stopping
                println("macOS process stopping not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux process stopping
                println("Linux process stopping not yet implemented")
            }
        }
    }

    actual fun createService() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                val currentDir = Path.of("").toAbsolutePath().toString()
                val file = "$currentDir\\app\\resources\\HardwareMonitor.exe"
                val command = listOf(
                    "cmd.exe",
                    "/c",
                    "sc create svcleanmeter displayname=\"CleanMeter Service\" binPath= $file start=auto group=LocalServiceNoNetworkFirewall"
                )
                ProcessBuilder().apply {
                    command(command)
                }.start()
            }
            Platform.MACOS -> {
                // TODO: Implement macOS service creation
                println("macOS service creation not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux service creation
                println("Linux service creation not yet implemented")
            }
        }
    }

    actual fun stopService() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                ProcessBuilder().apply {
                    command(
                        "cmd.exe",
                        "/c",
                        "sc stop svcleanmeter"
                    )
                }.start()
            }
            Platform.MACOS -> {
                // TODO: Implement macOS service stopping
                println("macOS service stopping not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux service stopping
                println("Linux service stopping not yet implemented")
            }
        }
    }

    actual fun deleteService() {
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                ProcessBuilder().apply {
                    command(
                        "cmd.exe",
                        "/c",
                        "sc delete svcleanmeter"
                    )
                }.start()
            }
            Platform.MACOS -> {
                // TODO: Implement macOS service deletion
                println("macOS service deletion not yet implemented")
            }
            Platform.LINUX -> {
                // TODO: Implement Linux service deletion
                println("Linux service deletion not yet implemented")
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