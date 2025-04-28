pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Versions declared **only** here:
        id("com.android.application") version "8.8.1"
        id("com.google.gms.google-services") version "4.3.15"
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
