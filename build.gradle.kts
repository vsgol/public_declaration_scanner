plugins {
    kotlin("jvm") version "2.1.10"
    id("application")
}

group = "com.github.vsgol"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.1.10")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}