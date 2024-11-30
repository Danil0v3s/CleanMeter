package br.com.firstsoft.core.os.hardwaremonitor

import br.com.firstsoft.core.os.util.isDev
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.util.concurrent.TimeUnit

object HardwareMonitorProcessManager {
    private var process: Process? = null
    private const val MAX_RETRIES = 1
    private var currentRetries = 0
    private var pollingJob: Job? = null

    init {
        currentRetries = 0
    }

    fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = if (isDev()) {
            "D:\\Projetos\\Personal\\PCMonitoR\\HardwareMonitor\\HardwareMonitor\\bin\\Release\\net8.0\\HardwareMonitor.exe"
        } else {
            "$currentDir\\app\\resources\\HardwareMonitor.exe"
        }

        process = ProcessBuilder().apply {
            command("cmd.exe", "/c", file)
        }.start()
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
        }.inheritIO().start()
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

    fun stop() {
        process?.apply {
            descendants().forEach(ProcessHandle::destroy)
            destroy()
        }
        process = null
    }

    private fun restart() {
        stop()
        start()
    }

    private fun observePollingTime() = CoroutineScope(Dispatchers.IO).launch {
        var lastPollTime = 0L
        var accumulator = 0L

        HardwareMonitorReader
            .currentData
            .cancellable()
            .map { it.LastPollTime }
            .collectLatest {
                accumulator += 500
                if (accumulator < TimeUnit.SECONDS.toMillis(5)) return@collectLatest

                accumulator = 0
                when {
                    lastPollTime != it -> {
                        lastPollTime = it
                        currentRetries = 0
                    }

                    lastPollTime == it && currentRetries < MAX_RETRIES -> {
                        currentRetries++
                        restart()
                    }
                }
            }
    }
}