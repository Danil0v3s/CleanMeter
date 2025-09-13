import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
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
    implementation(projects.core.updater)
    implementation(projects.core.designSystem)
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
                finalizedBy(compileMonitor)
            }
            tasks.named("runDistributable") {
                finalizedBy(compileMonitor)
            }
        }

        mainClass = "app.cleanmeter.target.desktop.DesktopMainKt"

        buildTypes.release.proguard {
            version.set("7.5.0")
        }

        nativeDistributions {
            val projectVersion: String by project

            targetFormats(TargetFormat.Exe, TargetFormat.Deb)

            packageName = "cleanmeter"
            packageVersion = projectVersion
            
            includeAllModules = true

            windows {
                iconFile.set(project.file("src/main/resources/imgs/favicon.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/imgs/logo.png"))
            }
        }
    }
}

val copyMonitorFiles = tasks.register<Copy>("copyMonitorFiles") {
    from("../../bin/")
    into(layout.buildDirectory.dir("compose/binaries/main/app/cleanmeter/app/resources"))
}

val compileMonitor = tasks.register<Exec>("compileMonitor") {
    finalizedBy(copyMonitorFiles)
    workingDir("../../../HardwareMonitor/")
    commandLine("dotnet", "publish", "-c", "Release", "-r", "win-x64", "-p:PublishAot=false")
}
