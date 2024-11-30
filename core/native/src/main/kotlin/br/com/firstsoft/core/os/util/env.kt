package br.com.firstsoft.core.os.util

fun isDev() = try {
    System.getenv("env") == "dev"
} catch (e: Exception) {
    false
}