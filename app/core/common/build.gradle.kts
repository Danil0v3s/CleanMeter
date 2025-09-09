plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines.core)
}