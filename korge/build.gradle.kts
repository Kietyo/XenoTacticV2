import com.soywiz.korge.gradle.korge

//plugins {
//	alias(libs.plugins.korge)
//}

apply<com.soywiz.korge.gradle.KorgeGradlePlugin>()

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

	add("commonMainImplementation", project(":ktruth"))
	add("commonTestImplementation", project(":ktruth"))

	//    add("commonMainImplementation", project(":Fleks"))
	add("commonMainImplementation", project(":XenoECS"))
}