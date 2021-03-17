import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("kapt") version "1.4.21"
}

group = "world.cepi.packer"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {

    implementation("com.velocitypowered:velocity-api:1.1.4")
    kapt("com.velocitypowered:velocity-api:1.1.4-SNAPSHOT")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}