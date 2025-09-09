plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)

    implementation("io.github.z4kn4fein:semver:2.0.0")
    implementation(projects.core.common)
    implementation(projects.core.native)
}