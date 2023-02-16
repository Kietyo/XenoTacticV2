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
    val kotlinxBenchmark: String by settings
    plugins {
        id("org.jetbrains.kotlinx.benchmark") version kotlinxBenchmark
    }
}

//include(":Client")
//include(":Server")
include(":korge")
include(":XenoTactic-GameLogic")
//include(":Fleks")

include("XenoECS")
project(":XenoECS").projectDir = file("C:\\Users\\kietm\\GitHub\\XenoECS")