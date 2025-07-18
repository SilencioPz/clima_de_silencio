pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
//    plugins {
//        id("com.android.application") version "8.6.0" apply false
//        id("org.jetbrains.kotlin.android") version "1.9.23"
//        id("com.google.dagger.hilt.android") version "2.48" apply false
//        id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
////        id("org.jetbrains.kotlin.plugin.compose") version "1.9.22"
//    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ClimaDeSilencioNoAr"
include(":app")
