package app.cleanmeter.core.os.hardwaremonitor

import app.cleanmeter.core.os.util.isDev
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.util.*

object HardwareMonitorProcessManager {
    private var process: Process? = null

    fun checkRuntime() {
        ProcessBuilder().apply {
            command("dotnet --list-runtimes")
        }.start()
    }

    fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = if (isDev()) {
            "$currentDir\\HardwareMonitor\\HardwareMonitor\\bin\\Release\\net8.0\\HardwareMonitor.exe"
        } else {
            "$currentDir\\app\\resources\\HardwareMonitor.exe"
        }

        process = ProcessBuilder().apply {
            command("cmd.exe", "/c", file)
        }.start()

        val scannerIn = Scanner(process!!.inputStream)
        val scannerErr = Scanner(process!!.errorStream)

        CoroutineScope(Dispatchers.IO).launch {
            while(scannerIn.hasNextLine()) {
                System.out.println(scannerIn.nextLine())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            while(scannerErr.hasNextLine()) {
                System.err.println(scannerErr.nextLine())
            }
        }
    }

    fun stop() {
        process?.apply {
            descendants().forEach(ProcessHandle::destroy)
            destroy()
        }
        process = null
    }

    fun createService() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\app\\resources\\HardwareMonitor.exe"
        val command = listOf(
            "cmd.exe",
            "/c",
            "sc create svcleanmeter displayname= \"CleanMeter Service\" binPath= $file start= auto group= LocalServiceNoNetworkFirewall")
        ProcessBuilder().apply {
            command(command)
        }.start()
    }

    fun stopService() {
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                "sc stop svcleanmeter"
            )
        }.start()
    }

    fun deleteService() {
        ProcessBuilder().apply {
            command(
                "cmd.exe",
                "/c",
                "sc delete svcleanmeter"
            )
        }.start()
    }

}