package br.com.firstsoft.core.os.hwinfo

import java.nio.file.Path

class HwInfoProcessManager {
    private var process: Process? = null

    fun start() {
        val currentDir = Path.of("").toAbsolutePath().toString()
        val file = "$currentDir\\app\\resources\\HWiNFO64.exe"
        process = ProcessBuilder().apply {
            command("cmd.exe", "/c", file)

        }.start()
    }

    fun stop() {
        process?.destroy()
    }
}