plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(libs.jna)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization)

    implementation(projects.core.common)
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}