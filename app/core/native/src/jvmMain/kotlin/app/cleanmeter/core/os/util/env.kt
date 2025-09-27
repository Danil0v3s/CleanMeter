package app.cleanmeter.core.os.util

actual fun isDev(): Boolean = System.getenv("env") == "dev"