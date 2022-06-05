package com.xenotactic.gamelogic.ecs

import kotlin.test.Test
import kotlin.test.assertEquals

internal class FamilyTest {


    @Test
    fun familyTest1() {
        val world = World()

        val family = world.createFamily(
            FamilyConfiguration(
                allOfComponents = listOf(TestComponent::class)
            )
        )

        assertEquals(family.getEntities().size, 0)

    }
}