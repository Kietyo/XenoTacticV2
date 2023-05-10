package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.MonsterComponent
import com.xenotactic.gamelogic.components.PathSequenceTraversalComponent
import com.xenotactic.gamelogic.components.VelocityComponent
import kotlin.time.Duration

class MonsterMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class,
            PathSequenceTraversalComponent::class,
            VelocityComponent::class,
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val traversal =
                world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal
            val movementVelocityComponent = world[it, VelocityComponent::class]
            val computedSpeedEffectComponent =
                world[it, com.xenotactic.gamelogic.components.ComputedSpeedEffectComponent::class]

            val distanceTraversed =
                movementVelocityComponent.calculateDistance(deltaTime) * computedSpeedEffectComponent.computedSpeedEffect

            traversal.traverse(distanceTraversed)
        }
    }
}