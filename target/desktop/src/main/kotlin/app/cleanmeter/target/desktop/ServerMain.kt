package app.cleanmeter.target.desktop

import app.cleanmeter.core.common.process.singleInstance
import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.os.ProcessManager
import app.cleanmeter.core.os.util.isDev
import app.cleanmeter.core.os.win32.WindowsService


fun main(vararg args: String) = singleInstance(args) {
    System.setProperty("awt.useSystemAAFontSettings","on");
    System.setProperty("swing.aatext", "true");
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
