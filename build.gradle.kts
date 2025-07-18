// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}
//
//allprojects {
//    repositories {
//        google()
//        mavenCentral()
//    }
//}