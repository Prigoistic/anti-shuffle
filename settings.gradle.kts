pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    plugins {
        id("com.android.application") version "8.1.1"
        id("com.android.library") version "8.1.1"
        id("org.jetbrains.kotlin.android") version "1.9.22"
        id("com.google.dagger.hilt.android") version "2.50"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "gallery_app"
include(":app")
