package app.cleanmeter.target.desktop

import app.cleanmeter.core.common.process.singleInstance
import app.cleanmeter.core.common.reporting.ApplicationParams
import app.cleanmeter.core.os.util.isDev
import app.cleanmeter.core.os.PlatformService
import app.cleanmeter.core.os.PREFERENCE_PERMISSION_CONSENT
import app.cleanmeter.core.os.PreferencesRepository
import app.cleanmeter.core.os.hardwaremonitor.HardwareMonitorProcessManager

fun main(vararg args: String) = singleInstance(args) {
    if (PreferencesRepository.getPreferenceBoolean(PREFERENCE_PERMISSION_CONSENT, false)) {
        if (isDev()) {
            HardwareMonitorProcessManager.start()
            Runtime.getRuntime().addShutdownHook(Thread {
                HardwareMonitorProcessManager.stop()
            })
        } else {
            KeyboardManager.registerKeyboardHook()
            if (!HardwareMonitorProcessManager.isServiceCreated()) {
                HardwareMonitorProcessManager.createService()
            }
        }
    }

    composeApp()
}
