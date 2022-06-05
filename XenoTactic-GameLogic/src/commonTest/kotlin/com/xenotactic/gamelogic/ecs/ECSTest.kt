package com.xenotactic.gamelogic.ecs

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ECSTest {



    @Test
    fun entityWithComponentGetsReturned() {
        val world = World()
        val component = TestComponent("test")

        val entity = world.addEntity {
            addOrReplaceComponent(component)
        }

        assertEquals(entity.getComponent(), component)
    }
}