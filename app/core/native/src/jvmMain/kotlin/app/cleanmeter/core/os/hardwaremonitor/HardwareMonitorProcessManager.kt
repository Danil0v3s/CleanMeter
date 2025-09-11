package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.Platform
import app.cleanmeter.core.os.getCurrentPlatform
import app.cleanmeter.core.os.util.isDev
import app.cleanmeter.core.os.win32.WinRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.*

actual object HardwareMonitorProcessManager {
    private var process: Process? = null

    val appDir: String
        get() {
            val currentDir = Path.of("").toAbsolutePath().toString()
            return if (!isDev()) {
                "$currentDir\\bin\\win-x64"
            } else {
                "$currentDir\\resources\\win-x64"
            }
        }

    actual fun start() {
        if (!isDev()) return
        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                val process = ProcessBuilder().apply {
                    command("cmd.exe", "/c", "$appDir\\HardwareMonitor.exe")
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
        if (!isDev()) return
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
        if (isDev()) return

        when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                val scCommand = "$appDir\\service-create.bat"

                val process = ProcessBuilder( "cmd", "/c", scCommand)
                    .redirectErrorStream(true)
                    .inheritIO()
                    .start()

                process.waitFor()
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
                val scCommand = "$appDir\\service-stop.bat"

                val process = ProcessBuilder( "cmd", "/c", scCommand)
                    .redirectErrorStream(true)
                    .inheritIO()
                    .start()

                process.waitFor()
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
                val scCommand = "$appDir\\service-delete.bat"

                val process = ProcessBuilder( "cmd", "/c", scCommand)
                    .redirectErrorStream(true)
                    .inheritIO()
                    .start()

                process.waitFor()
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

    actual fun isServiceCreated(): Boolean {
        return when (getCurrentPlatform()) {
            Platform.WINDOWS -> {
                val proc = ProcessBuilder("sc", "query", "\"CleanMeterHardwareMonitor\"")
                    .redirectErrorStream(true)
                    .start()
                val input = BufferedReader(InputStreamReader(proc.inputStream)).lineSequence().toList()

                return proc.exitValue() == 0
            }

            Platform.MACOS -> {
                println("macOS service deletion not yet implemented")
                false
            }

            Platform.LINUX -> {
                println("Linux service deletion not yet implemented")
                false
            }
        }
    }
}
