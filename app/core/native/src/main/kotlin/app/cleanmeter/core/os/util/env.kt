package app.cleanmeter.core.os.util

fun isDev() = System.getenv("env") == "dev"