package com.xenotactic.korge.components

import com.soywiz.korio.lang.portableSimpleName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.test.Test

internal class ComponentsTest {


    @Test
    fun test() {
        val component = com.xenotactic.gamelogic.components.BottomLeftPositionComponent(1, 2)
        println(component::class.qualifiedName)
        println(component::class.portableSimpleName)
        println(component::class.simpleName)

        println(com.xenotactic.gamelogic.components.BottomLeftPositionComponent.serializer().descriptor.serialName)
    }
}