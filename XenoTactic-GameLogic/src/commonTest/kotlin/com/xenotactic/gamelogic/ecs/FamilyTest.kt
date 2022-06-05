package com.xenotactic.gamelogic.ecs

import kotlin.test.Test
import kotlin.test.assertEquals

internal class FamilyTest {


    @Test
    fun familyTest1() {
        val world = World()

        val family = world.addFamily(
            FamilyConfiguration(
                allOfComponents = listOf(TestComponent::class)
            )
        )

        assertEquals(family.getEntities().size, 0)

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        assertEquals(family.getEntities().size, 1)
    }
}