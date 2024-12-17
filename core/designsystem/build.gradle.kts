plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(compose.desktop.currentOs)
}