@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvmToolchain(17)
    
    jvm()
    
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization)
                implementation("io.github.z4kn4fein:semver:2.0.0")
                implementation(projects.core.common)
            }
        }
        
        jvmMain {
            dependencies {
                api(libs.jna)
            }
        }
    }
}