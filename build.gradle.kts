
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

repositories {
    mavenCentral()
}