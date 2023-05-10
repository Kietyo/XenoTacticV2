package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.getCenterPoint
import kotlin.time.Duration

class MonsterComputeSpeedEffectSystem(
    val engine: Engine
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.MonsterComponent::class,
            com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class,
        )
    )

    private val world = engine.gameWorld.world

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { entityId ->
            val pathSequenceTraversalComponent = world[entityId, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class]
            val computedSpeedEffectComponent = world[entityId, com.xenotactic.gamelogic.components.ComputedSpeedEffectComponent::class]

            val currentPosition = pathSequenceTraversalComponent.pathSequenceTraversal.currentPosition

            computedSpeedEffectComponent.computedSpeedEffect =
                engine.gameWorld.speedAreaFamily.getSequence().fold(1.0) { acc, it ->
                    val speedAreaEffectComponent = world[it, com.xenotactic.gamelogic.components.EntitySpeedAreaComponent::class]
                    val bottomLeftPositionComponent = world[it, com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class]
                    val sizeComponent = world[it, com.xenotactic.gamelogic.components.SizeComponent::class]
                    val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)
                    val radius = sizeComponent.toRadius()
                    val distance = currentPosition.distanceTo(centerPoint)
                    if (distance <= radius) {
                        acc * speedAreaEffectComponent.speedEffect
                    } else {
                        acc
                    }
                }
        }
    }
}