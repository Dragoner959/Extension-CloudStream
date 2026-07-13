plugins {
    kotlin("jvm") version "2.3.21"
}

group = "com.githubextension"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.recloudstream.cloudstream:library-jvm:4.8.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("com.github.Blatzar:NiceHttp:0.4.18")
}

kotlin {
    jvmToolchain(17)
}
