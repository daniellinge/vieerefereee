buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.1")
        classpath(kotlin("gradle-plugin", version = "1.9.0"))
    }
}

plugins {
    id("com.android.application") version "8.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

allprojects {
    repositories {
     }
}
