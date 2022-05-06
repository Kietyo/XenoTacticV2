val main_class = "CoolMainKt"

project.setProperty("mainClassName", main_class)

plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.2"
    application
}

application {
    mainClass.set(main_class)
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

group = "com.xenotactic.gamelogic"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

val korgeVersion = "2.6.3"
val gitliveVersion = "1.4.3"
val ktorVersion = "2.0.1"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.soywiz.korlibs.korma:korma:$korgeVersion")
                implementation("com.soywiz.korlibs.korio:korio:$korgeVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")

                implementation("com.google.firebase:firebase-admin:8.1.0")

                compileOnly("com.google.api-client:google-api-client:1.31.5")


                //                implementation("com.google.firebase:firebase-database-ktx:20.0.4")

                //                implementation("dev.gitlive:firebase-app:$gitliveVersion")
                //                implementation("dev.gitlive:firebase-common:$gitliveVersion")
                //                implementation("dev.gitlive:firebase-firestore:$gitliveVersion")
                //                implementation("dev.gitlive:firebase-auth:$gitliveVersion")
                //                implementation("dev.gitlive:firebase-database:$gitliveVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        val jsTest by getting
//        val nativeMain by getting {
//            dependencies {
//                implementation("io.ktor:ktor-client-curl:$ktorVersion")
//            }
//        }
//        val nativeTest by getting
    }
}

benchmark {
    targets {
        register("jvm")
    }
}
