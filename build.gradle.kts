import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

//configureAutoVersions()

repositories {
    mavenCentral()
}