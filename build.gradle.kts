// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services)
        // hilt
        classpath(libs.hilt.android.gradle.plugin)
        // NOTE: Do not place your application dependencies here; they belong in the individual module build.gradle files
    }
}

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://jitpack.io")
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
