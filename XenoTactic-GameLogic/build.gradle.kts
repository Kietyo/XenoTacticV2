//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//apply<KorgeGradlePlugin>()

//apply(plugin = "kotlin")

//val main_class = "CoolMainKt"
//
//project.setProperty("mainClassName", main_class)

//repositories {
//    mavenLocal()
//    mavenCentral()
//    google()
//    maven { url = uri("https://plugins.gradle.org/m2/") }
//}

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.2"
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

//apply(plugin = "plugin.serialization")

//application {
//    mainClass.set(main_class)
//}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

group = "com.xenotactic.gamelogic"
version = "1.0.0"

val gitliveVersion = "1.4.3"
val ktorVersion = "2.0.1"

val korgePluginVersion: String by project

//dependencies {
//    add("commonMainImplementation","com.soywiz.korlibs.korma:korma:$korgeVersion")
//    add("commonMainImplementation","com.soywiz.korlibs.korio:korio:$korgeVersion")
//    add("commonMainImplementation","org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
//    add("commonMainImplementation","org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
//    add("commonMainImplementation","org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
//    add("commonMainImplementation","org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")
//}

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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")


                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")

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
                implementation(kotlin("test"))
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