import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val copyMonitorFiles = tasks.register<Copy>("copyMonitorFiles") {
    from("../../HardwareMonitor/HardwareMonitor/bin/Release/net8.0")
    into(layout.buildDirectory.dir("compose/binaries/main/app/cleanmeter/app/resources"))
}

val compileMonitor = tasks.register<Exec>("compileMonitor") {
    finalizedBy(copyMonitorFiles)
    workingDir("../../HardwareMonitor/")
    commandLine("dotnet", "build", "--configuration", "Release")
}

val copyPresentmonToResources = tasks.register<Copy>("copyPresentmonToResources") {
    finalizedBy(compileMonitor)

    from("../../presentmon")
    into(layout.buildDirectory.dir("compose/binaries/main/app/cleanmeter/app/resources"))
}

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    implementation(libs.jnativehook)
    implementation(libs.kotlinx.serialization)

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material)
    implementation(libs.viewModel)

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

        afterEvaluate {
            tasks.named("createDistributable") {
                finalizedBy(copyPresentmonToResources)
            }
        }

        mainClass = "br.com.firstsoft.target.server.ServerMainKt"

        buildTypes.release.proguard {
            version.set("7.5.0")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Deb)

            packageName = "cleanmeter"
            packageVersion = "0.0.8"

            windows {
                iconFile.set(project.file("src/main/resources/imgs/favicon.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/imgs/logo.png"))
            }
        }
    }
}
