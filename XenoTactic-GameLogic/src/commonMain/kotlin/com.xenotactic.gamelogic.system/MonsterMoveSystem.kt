package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import kotlin.time.Duration

class MonsterMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.MonsterComponent::class,
            com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class,
            com.xenotactic.gamelogic.components.VelocityComponent::class,
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val traversal =
                world[it, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class].pathSequenceTraversal
            val movementVelocityComponent = world[it, com.xenotactic.gamelogic.components.VelocityComponent::class]
            val computedSpeedEffectComponent =
                world[it, com.xenotactic.gamelogic.components.ComputedSpeedEffectComponent::class]

            traversal.traverse(movementVelocityComponent.velocity * computedSpeedEffectComponent.computedSpeedEffect)
        }
    }
}