//import com.soywiz.korge.gradle.*

//buildscript {
//    val korgePluginVersion: String by project
//
//    repositories {
//        mavenLocal()
//        mavenCentral()
//        google()
//        maven { url = uri("https://plugins.gradle.org/m2/") }
//    }
//    dependencies {
//        classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
//    }
//}

//apply<KorgeGradlePlugin>()

//korge {
//    id = "com.kietyo.xenotactic.korge.xenotactic"
//    supportExperimental3d()
//    supportBox2d()
//    supportFleks()
//    // To enable all targets at once
//
//    //targetAll()
//
//    // To enable targets based on properties/environment variables
//    //	targetDefault()
//
//    // To selectively enable targets
//
//    targetJvm()
//    targetJs()
//    targetDesktop()
//    //	targetIos()
//    //		targetAndroidIndirect() // targetAndroidDirect()
//    //	targetAndroidDirect()
//
//    serializationJson()
//
//    project.dependencies {
////        add("commonMainApi", "com.xenotactic.gamelogic:XenoTactic-GameLogic:1.0.0")
////        project(":XenoTactic-GameLogic")
//        add("commonMainImplementation", project(":XenoTactic-GameLogic"))
//    }
//
//    entrypoint("Debug", "DebugMain")
//}

import com.soywiz.korge.gradle.*

apply<KorgeGradlePlugin>()

korge {
    id = "com.xenotactic.korge"

    serializationJson()
}

dependencies {
    add("commonMainImplementation", project(":XenoTactic-GameLogic"))
}