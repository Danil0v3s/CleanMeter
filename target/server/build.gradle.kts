import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val createShortcut = tasks.register<Exec>("createShortcut") {
    commandLine(
        "cmd.exe",
        "/c",
        "mklink",
        layout.buildDirectory.file("compose/binaries/main/app/cleanmeter/Launcher.lnk").get().asFile.absolutePath,
        layout.buildDirectory.file("compose/binaries/main/app/cleanmeter/app/resources/Launcher.exe").get().asFile.absolutePath
    )
}
val copyLauncherFiles = tasks.register<Copy>("copyLauncherFiles") {
    finalizedBy(createShortcut)
    from("../../Launcher/Launcher/bin/Release/net8.0")
    into(layout.buildDirectory.dir("compose/binaries/main/app/cleanmeter/app/resources"))
}
val compileLauncher = tasks.register<Exec>("compileLauncher") {
    finalizedBy(copyLauncherFiles)
    workingDir("../../Launcher/")
    commandLine("dotnet", "build", "--configuration", "Release")
}
val copyHwinfoToResources = tasks.register<Copy>("copyHwinfoToResources") {
    finalizedBy(compileLauncher)

    from("src/main/resources/hwinfo")
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
                finalizedBy(copyHwinfoToResources)
            }
        }

        mainClass = "br.com.firstsoft.target.server.ServerMainKt"

        buildTypes.release.proguard {
            version.set("7.5.0")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Deb)

            packageName = "cleanmeter"
            packageVersion = "0.0.5"

            windows {
                iconFile.set(project.file("src/main/resources/imgs/favicon.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/imgs/logo.png"))
            }
        }
    }
}
