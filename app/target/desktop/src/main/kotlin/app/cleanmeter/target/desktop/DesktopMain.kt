package app.cleanmeter.target.desktop

import app.cleanmeter.core.common.process.singleInstance
import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.os.ProcessManager
import app.cleanmeter.core.os.util.isDev
import app.cleanmeter.core.os.win32.WindowsService
import app.cleanmeter.core.os.PREFERENCE_PERMISSION_CONSENT
import app.cleanmeter.core.os.PreferencesRepository

fun main(vararg args: String) = singleInstance(args) {
    if (PreferencesRepository.getPreferenceBoolean(PREFERENCE_PERMISSION_CONSENT, false)) {
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
    }

    composeApp()
}
