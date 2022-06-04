package com.xenotactic.gamelogic.ecs

import kotlin.test.Test

internal class ECSTest {

    @Test
    fun blah() {
        val world = World()


        val entity = world.addEntity {
            addComponent(PreSelectComponent)
        }
    }
}