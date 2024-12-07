package br.com.firstsoft.target.server

import br.com.firstsoft.core.common.process.singleInstance
import br.com.firstsoft.core.common.reporting.ApplicationParams
import br.com.firstsoft.core.os.ProcessManager
import br.com.firstsoft.core.os.util.isDev
import br.com.firstsoft.core.os.win32.WindowsService


fun main(vararg args: String) = singleInstance(args) {
    WindowsService.tryElevateProcess(ApplicationParams.isAutostart)

    if (isDev()) {
        Runtime.getRuntime().addShutdownHook(Thread {
            ProcessManager.stop()
        })
    } else {
        KeyboardManager.registerKeyboardHook()
    }

    if (!ApplicationParams.isAutostart) {
        ProcessManager.start()
    }

    composeApp()
}
