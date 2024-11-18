package br.com.firstsoft.core.common.reporting

import java.io.File

fun logException(throwable: Throwable) {
    File("cleanmeter.error.${System.currentTimeMillis()}.log").printWriter()
        .use { it.print(throwable.stackTraceToString()) }
}

fun setDefaultUncaughtExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        try {
            throwable.printStackTrace()
            logException(throwable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}