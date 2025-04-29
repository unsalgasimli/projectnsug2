pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // versions declared here only
        id("com.android.application")      version "8.8.1"
        id("com.google.gms.google-services") version "4.3.15"
        id("androidx.navigation.safeargs") version "2.7.0"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "projectnsug2"
include(":app")
