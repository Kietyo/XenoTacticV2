package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.components.PathSequenceTraversalComponent
import com.xenotactic.gamelogic.components.UIEntityViewComponent
import kotlin.time.Duration

class MonsterRemoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.MonsterComponent::class, com.xenotactic.gamelogic.components.UIEntityViewComponent::class,
            com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getNewList().forEach {
            val traversal = world[it, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class].pathSequenceTraversal
            if (traversal.isTraversalFinished()) {
                world.removeEntity(it)
            }
        }
    }
}