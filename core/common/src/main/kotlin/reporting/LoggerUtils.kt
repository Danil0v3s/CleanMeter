package reporting

import java.io.File

fun setDefaultUncaughtExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        try {
            File("cleanmeter.error.${System.currentTimeMillis()}.log").printWriter()
                .use { it.print(throwable.stackTraceToString()) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}