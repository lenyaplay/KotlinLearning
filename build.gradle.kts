plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.3.20"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    compileOnly("com.google.android:android:4.1.1.4")
    testImplementation("com.google.android:android:4.1.1.4")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}