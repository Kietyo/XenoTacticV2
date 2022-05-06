val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val main_class = "com.example.ApplicationKt"

project.setProperty("mainClassName", main_class)

plugins {
    application
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.google.cloud.tools.jib") version "2.7.1"
    `maven-publish`
}

group = "com.xenotactic.server.api"
version = "1.0.0"
application {
    mainClass.set(main_class)
}

repositories {
    mavenCentral()
    mavenLocal()
}

val projectId = project.findProperty("projectId") ?: "ktor-hello-world-test"
val image = "gcr.io/$projectId/xenotactic_api_server"

jib {
    to.image = image

    container {
        ports = listOf("8080")
        mainClass = main_class
        // good defaults intended for Java 8 (>= 8u191) containers
        jvmFlags = listOf(
            "-server",
            "-Djava.awt.headless=true",
            "-XX:InitialRAMFraction=2",
            "-XX:MinRAMFraction=2",
            "-XX:MaxRAMFraction=2",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-XX:+UseStringDeduplication"
        )
    }
}

val deploy by tasks.registering(Exec::class) {
    commandLine = "gcloud run deploy ktor-hello-world --image $image --project $projectId --platform managed --region us-central1".split(" ")
    dependsOn += tasks.findByName("jib")
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}