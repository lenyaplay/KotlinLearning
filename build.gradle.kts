plugins {
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.serialization") version "2.4.0"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // сериализация нужна только тестам: кейсы валидатора скобок читаются из JSON
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // стабы Android SDK: дают настоящие типы android.content.* на компиляции, в артефакт не попадают.
    // isTransitive = false — иначе приезжают httpclient 4.0.1, xerces (дубликаты пакетов JDK), org.json
    // и ещё четыре артефакта, ни один из которых не нужен ради интерфейса SharedPreferences
    compileOnly("com.google.android:android:4.1.1.4") { isTransitive = false }
    testImplementation("com.google.android:android:4.1.1.4") { isTransitive = false }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    // иначе кириллица в XML-отчётах превращается в кракозябры
    defaultCharacterEncoding = "UTF-8"
}
