package com.xenotactic.gamelogic.ecs

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ECSTest {

    data class TestComponent(val value: String)

    @Test
    fun entityWithComponentGetsReturned() {
        val world = World()
        val component = TestComponent("test")

        val entity = world.addEntity {
            addComponent(component)
        }

        assertEquals(entity.getComponent(), component)
    }
}