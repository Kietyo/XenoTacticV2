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

//include(":Client")
//include(":Server")
include(":korge")
include(":XenoTactic-GameLogic")
//include(":Fleks")

include("XenoECS")
project(":XenoECS").projectDir = file("C:\\Users\\kietm\\GitHub\\XenoECS")