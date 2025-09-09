import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val copyPresentMon = tasks.register<Copy>("copyPresentMon") {
    from("../../../HardwareMonitor/HardwareMonitor/bin/Release/net8.0/win-x64/presentmon")
    into(layout.buildDirectory.dir("compose/binaries/main/app/cleanmeter/app/resources"))
}

val copyMonitorFiles = tasks.register<Copy>("copyMonitorFiles") {
//    finalizedBy(copyPresentMon)
    from("../../../HardwareMonitor/HardwareMonitor/bin/Release/net8.0/win-x64/native")
    into(layout.buildDirectory.dir("compose/binaries/main/app/cleanmeter/app/resources"))
}

val compileMonitor = tasks.register<Exec>("compileMonitor") {
    finalizedBy(copyMonitorFiles)
    workingDir("../../../HardwareMonitor/")
    commandLine("dotnet", "publish", "-c", "Release", "-r", "win-x64", "-p:PublishAot=true")
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
