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
    repositories {
        println("Setting up repo in settings.gradle.kts")
        mavenLocal(); mavenCentral(); google(); gradlePluginPortal()
    }
    val kotlinVersion: String by settings
    val kotlinxBenchmark: String by settings
//    val composeVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlinx.benchmark") version kotlinxBenchmark
    }
}

//plugins {
//    //id("com.soywiz.kproject.settings") version "0.0.1-SNAPSHOT"
//    id("com.soywiz.kproject.settings") version "0.3.1"
//}

//kproject("./deps")

//include(":Client")
//include(":Server")
include(":korge")
include(":XenoTactic-GameLogic")
//include(":Fleks")

include("XenoECS")
//project(":XenoECS").projectDir = file("..\\XenoECS")

include("ktruth")
//project(":ktruth").projectDir = file("..\\ktruth")
//include("korge-compose")
//project(":korge-compose").projectDir = file("..\\korge-compose\\korge-compose")

include("XenoKorgeCommon")
project(":XenoKorgeCommon").projectDir = file("..\\XenoKorgeCommon")

