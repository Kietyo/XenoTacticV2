//enableFeaturePreview("GRADLE_METADATA")



//pluginManagement {
//    val korgePluginVersion: String by settings
//
//    repositories {
//        mavenLocal()
//        mavenCentral()
//        google()
//        gradlePluginPortal()
//    }
//    plugins {
////        id("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
//        id("com.soywiz.korlibs.korge.plugins") version("$korgePluginVersion")
//    }
//}

pluginManagement {
    val kotlinVersion: String by settings
    val kotlinxBenchmark: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlinx.benchmark") version kotlinxBenchmark
        id("com.soywiz.kproject.settings") version "0.0.6"
    }
}

//include(":Client")
//include(":Server")
include(":korge")
include(":XenoTactic-GameLogic")
//include(":Fleks")

include("XenoECS")
project(":XenoECS").projectDir = file("C:\\Users\\kietm\\GitHub\\XenoECS")