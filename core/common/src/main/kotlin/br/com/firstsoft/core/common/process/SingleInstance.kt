package br.com.firstsoft.core.common.process

import br.com.firstsoft.core.common.reporting.ApplicationParams
import br.com.firstsoft.core.common.reporting.setDefaultUncaughtExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.PrintStream
import java.net.ServerSocket
import java.util.*
import kotlin.system.exitProcess

fun singleInstance(args: Array<out String>, block: () -> Unit) {
    if(isAppAlreadyRunning()) {
        exitProcess(0)
    }

    ApplicationParams.parse(args)

    setDefaultUncaughtExceptionHandler()

    if (ApplicationParams.isVerbose) {
        val startTime = Date().time
        val printStream = PrintStream(FileOutputStream("out.$startTime.txt", true))
        System.setOut(printStream)
        System.setErr(printStream)
    }

    block()
}

private fun isAppAlreadyRunning() = try {
    ServerSocket(1337).apply {
        Runtime.getRuntime().addShutdownHook(Thread {
            close()
        })

        CoroutineScope(Dispatchers.IO).launch {
            try {
                accept()
            } catch (_: Exception) {
                // consume the exception of accept since we do not really care if the socket was shutdown
            }
        }
    }
    false
} catch (ex: Exception) {
    true
}