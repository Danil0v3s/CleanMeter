plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(libs.jna)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization)
    implementation("io.github.z4kn4fein:semver:2.0.0")

    implementation(projects.core.common)
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}