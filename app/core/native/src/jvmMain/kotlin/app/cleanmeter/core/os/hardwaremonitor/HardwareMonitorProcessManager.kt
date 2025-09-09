package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.util.isDev
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.file.Path
import java.util.*

actual object HardwareMonitorProcessManager {
    private var process: Process? = null

    actual fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = if (isDev()) {
            "$currentDir\\bin\\HardwareMonitor.exe"
        } else {
            "$currentDir\\app\\resources\\HardwareMonitor.exe"
        }

        process = ProcessBuilder().apply {
            command("cmd.exe", "/c", file)
        }.start()

        val scannerIn = Scanner(process!!.inputStream)
        val scannerErr = Scanner(process!!.errorStream)

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
    }

    actual fun stop() {
        process?.apply {
            descendants().forEach(ProcessHandle::destroy)
            destroy()
        }
        process = null
    }

    actual fun createService() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\app\\resources\\HardwareMonitor.exe"
        val command = listOf(
            "cmd.exe",
            "/c",
            "sc create svcleanmeter displayname= \"CleanMeter Service\" binPath= $file start= auto group= LocalServiceNoNetworkFirewall"
        )
        ProcessBuilder().apply {
            command(command)
        }.start()
    }

    actual fun stopService() {
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                "sc stop svcleanmeter"
            )
        }.start()
    }

    actual fun deleteService() {
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                "sc delete svcleanmeter"
            )
        }.start()
    }

}