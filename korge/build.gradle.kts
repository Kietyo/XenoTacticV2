import korlibs.korge.gradle.*

//plugins {
//	alias(libs.plugins.korge)
//}

apply<KorgeGradlePlugin>()

korge {
	id = "com.kietyo.xenotactic.korge.xenotactic"

// To enable all targets at once

//	targetAll()

// To enable targets based on properties/environment variables
	//targetDefault()

// To selectively enable targets
	
	targetJvm()
	targetJs()
//	targetDesktop()
//	targetIos()
//	targetAndroidIndirect() // targetAndroidDirect()
	//targetAndroidDirect()

	serializationJson()
	entrypoint("Debug", "DebugMain")
	entrypoint("DebugMain2", "DebugMain2")
	entrypoint("DebugMain3", "DebugMain3")
	entrypoint("DebugMain4", "DebugMain4")
	entrypoint("DebugMain5", "DebugMain5")
	entrypoint("DebugMain6", "DebugMain6")

}

dependencies {
	add("commonMainImplementation", project(":XenoTactic-GameLogic"))
	add("commonTestImplementation", project(":XenoTactic-GameLogic"))

	add("commonMainImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC")
	add("commonTestImplementation", "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

	add("commonMainImplementation", project(":ktruth"))
	add("commonTestImplementation", project(":ktruth"))

	//    add("commonMainImplementation", project(":Fleks"))
	add("commonMainImplementation", project(":XenoECS"))
	add("commonMainApi", project(":deps"))
}