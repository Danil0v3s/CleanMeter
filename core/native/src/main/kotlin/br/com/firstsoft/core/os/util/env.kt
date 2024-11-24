package br.com.firstsoft.core.os.util

fun isDev() = System.getenv("env") == "dev"