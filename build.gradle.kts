
buildscript {
    val korgePluginVersion: String by project

    repositories {
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