plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.serialization)
    implementation(libs.jackson.module)
    implementation(libs.jackson.dataformat)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}