plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "br.com.firstsoft.kracing"
version = "0.0.1"


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