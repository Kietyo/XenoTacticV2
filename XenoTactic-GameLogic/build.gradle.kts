val korgePluginVersion: String by project
val kotlinxSerialization: String by project
val kotlinxBenchmark: String by project
val kotlinVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
    id("org.jetbrains.kotlinx.benchmark")
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

//application {
//    mainClass.set(main_class)
//}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

group = "com.xenotactic.gamelogic"
version = "1.0.0"

val gitliveVersion = "1.4.3"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks{
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xskip-prerelease-check")
        }
    }
}

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
//                cssSupport.enabled = true
            }
            testTask {
                enabled = false
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
                implementation("com.soywiz.korlibs.korma:korma:$korgePluginVersion")
                implementation("com.soywiz.korlibs.korio:korio:$korgePluginVersion")
                implementation("com.soywiz.korlibs.korge2:korge:$korgePluginVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerialization")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinxSerialization")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")


                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:$kotlinxBenchmark")
                implementation(project(":XenoECS"))
                implementation(project(":ktruth"))

                implementation(kotlin("test"))

//                implementation("io.ktor:ktor-client-core:$ktorVersion")
//                implementation("com.google.firebase:firebase-admin:8.1.0")
//                compileOnly("com.google.api-client:google-api-client:1.31.5")

//                implementation("io.ktor:ktor-client-cio:$ktorVersion")


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
                implementation("com.soywiz.korlibs.korma:korma:$korgePluginVersion")
                implementation(kotlin("test"))
                implementation(project(":ktruth"))
//                implementation(kotlin("test-js"))
            }
        }
        val jvmMain by getting {
            dependencies {
//                implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
//                implementation("io.ktor:ktor-client-cio-js:$ktorVersion")
//                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        val jsTest by getting
        val nativeMain by getting {
            dependencies {
//                implementation("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }
        val nativeTest by getting
    }
}

benchmark {
    targets {
        register("jvm")
    }
}