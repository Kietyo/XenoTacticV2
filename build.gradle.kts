
buildscript {
    val korgePluginVersion: String by project

    repositories {
        println("Setting up buildscript>repositories>build.gradle.kts")
        mavenLocal()
        mavenCentral()
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")

//        classpath("korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
//        classpath("korlibs.korge:korlibs.korge.gradle.plugin:$korgePluginVersion")
//        classpath("korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
//        classpath("korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
    }
}

plugins {
    id("org.jetbrains.compose") version "1.4.0"
    id("com.soywiz.korge") version "4.0.0"
}


//allprojects {
//    repositories {
//        println("Setting up allprojects>repositories>build.gradle.kts")
//        mavenLocal()
//        mavenCentral()
//        google()
//        gradlePluginPortal()
//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//    }
//}

//tasks{
//    withType<KotlinCompile> {
//        kotlinOptions {
//            freeCompilerArgs += listOf("-Xskip-prerelease-check")
//        }
//    }
//}

//tasks{
//    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions {
//            println("Updating free compiler args.")
//            freeCompilerArgs += listOf(
//                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
//                "suppressKotlinVersionCompatibilityCheck=1.8.21")
//        }
//    }
//}

compose {
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.8.21")
    kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.8.20"))
}

repositories {
    mavenCentral()
}