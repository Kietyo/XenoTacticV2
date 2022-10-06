package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.MonsterComponent
import com.xenotactic.korge.components.PathSequenceTraversalComponent
import com.xenotactic.korge.components.UIMapEntityComponent
import kotlin.time.Duration

class MonsterRemoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class, UIMapEntityComponent::class,
            PathSequenceTraversalComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getNewList().forEach {
            val traversal = world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal
            if (traversal.isTraversalFinished()) {
                world.removeEntity(it)
            }
        }
    }
}