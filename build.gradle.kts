buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.1.0")
        classpath("com.github.recloudstream:gradle:81b1d424d2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    }
}

plugins {
    id("com.android.library")
    kotlin("android") version "2.3.21"
}

apply(plugin = "com.lagradost.cloudstream3.gradle")

group = "com.githubextension"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("com.github.recloudstream.cloudstream:library-jvm:4.8.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("com.github.Blatzar:NiceHttp:0.4.18")
}

android {
    namespace = "com.githubextension.cloudstream"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

cloudstream {
    setRepo("https://github.com/Dragoner959/Extension-CloudStream")
    authors = listOf("Dragoner959")
}
