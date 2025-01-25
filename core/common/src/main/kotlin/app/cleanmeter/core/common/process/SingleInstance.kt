package app.cleanmeter.core.common.process

import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.common.reporting.setDefaultUncaughtExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ServerSocket
import kotlin.system.exitProcess

fun singleInstance(args: Array<out String>, block: () -> Unit) {
    if(isAppAlreadyRunning()) {
        exitProcess(0)
    }

    ApplicationParams.parse(args)

    setDefaultUncaughtExceptionHandler()

    block()
}

private fun isAppAlreadyRunning() = try {
    ServerSocket(42069).apply {
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
