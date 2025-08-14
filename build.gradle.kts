// Project-level build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false

}

buildscript {
    repositories {
        google() // Ensure Google repository is included
        mavenCentral()
    }
    dependencies {
        // Declare Hilt version directly in the buildscript dependencies
        classpath(libs.google.services) // Google services
    }
}


