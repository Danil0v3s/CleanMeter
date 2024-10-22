import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

group = "br.com.firstsoft"
version = "0.0.1"

dependencies {
    implementation(libs.jnativehook)
    implementation(libs.kotlinx.serialization)

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material)

    implementation(projects.core.common)
    implementation(projects.core.native)
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}

compose.desktop {
    application {
        mainClass = "br.com.firstsoft.target.server.ServerMainKt"

        buildTypes.release.proguard {
            isEnabled = false
            optimize.set(false)
        }


        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Deb)

            packageName = "Clean Meter"
            packageVersion = "0.0.1"

            windows {
                iconFile.set(project.file("src/main/resources/imgs/favicon.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/imgs/logo.png"))
            }
        }
    }
}