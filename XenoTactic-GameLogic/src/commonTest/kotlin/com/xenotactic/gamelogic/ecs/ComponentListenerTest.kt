package com.xenotactic.gamelogic.ecs

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ComponentListenerTest {

    @Test
    fun addEntityBeforeAddingListenerDoesntTriggerListener() {
        val world = World()

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entity: Entity, component: TestComponent) {
                onAddTriggered = true
            }

            override fun onRemove(entity: Entity, component: TestComponent) {
                TODO("Not yet implemented")
            }

        })

        assertFalse(onAddTriggered)
    }

    @Test
    fun addEntityTriggersListener() {
        val world = World()

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entity: Entity, component: TestComponent) {
                println("Added component!: $component")
                onAddTriggered = true
            }

            override fun onRemove(entity: Entity, component: TestComponent) {
                TODO("Not yet implemented")
            }

        })

        assertFalse(onAddTriggered)

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        assertTrue(onAddTriggered)
    }

    @Test
    fun addComponentToCreatedEntityResultsInListenerTriggered() {
        val world = World()

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entity: Entity, component: TestComponent) {
                println("Added component!: $component")
                onAddTriggered = true
            }

            override fun onRemove(entity: Entity, component: TestComponent) {
                TODO("Not yet implemented")
            }

        })

        assertFalse(onAddTriggered)

        val entity = world.addEntity()

        assertFalse(onAddTriggered)

        entity.addOrReplaceComponent(TestComponent("test"))

        assertTrue(onAddTriggered)
    }
}