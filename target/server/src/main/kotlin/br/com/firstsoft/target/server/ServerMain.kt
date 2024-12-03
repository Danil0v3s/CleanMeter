package br.com.firstsoft.target.server

import br.com.firstsoft.core.common.process.singleInstance
import br.com.firstsoft.core.common.reporting.ApplicationParams
import br.com.firstsoft.core.os.ProcessManager
import br.com.firstsoft.core.os.util.isDev
import br.com.firstsoft.core.os.win32.WindowsService
import kotlinx.coroutines.channels.Channel

fun main(vararg args: String) = singleInstance(args) {
    WindowsService.tryElevateProcess(ApplicationParams.isAutostart)

    val channel = Channel<Unit>()

    if (isDev()) {
        Runtime.getRuntime().addShutdownHook(Thread {
            ProcessManager.stop()
        })
    } else {
        registerKeyboardHook(channel)
    }

    if (!ApplicationParams.isAutostart) {
        ProcessManager.start()
    }

    composeApp(
        callbackChannel = channel,
    )
}
