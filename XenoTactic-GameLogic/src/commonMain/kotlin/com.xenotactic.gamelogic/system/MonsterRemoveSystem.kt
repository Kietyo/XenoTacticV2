package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.components.PathSequenceTraversalComponent
import kotlin.time.Duration

class MonsterRemoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class,
            PathSequenceTraversalComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getNewList().forEach {
            val traversal = world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal
            if (traversal.isTraversalFinished()) {
                world.modifyEntity(it) {
                    removeThisEntity()
                }
            }
        }
    }
}