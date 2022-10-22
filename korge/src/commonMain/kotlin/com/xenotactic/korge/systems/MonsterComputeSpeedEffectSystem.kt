package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.korge.components.*
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.korge_utils.getCenterPoint
import kotlin.time.Duration

class MonsterComputeSpeedEffectSystem(
    val engine: Engine
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class,
            PathSequenceTraversalComponent::class,
        )
    )

    private val world = engine.gameWorld.world

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { entityId ->
            val pathSequenceTraversalComponent = world[entityId, PathSequenceTraversalComponent::class]
            val computedSpeedEffectComponent = world[entityId, ComputedSpeedEffectComponent::class]

            val currentPosition = pathSequenceTraversalComponent.pathSequenceTraversal.currentPosition

            computedSpeedEffectComponent.computedSpeedEffect =
                engine.gameWorld.speedAreaFamily.getSequence().fold(1.0) { acc, it ->
                    val speedAreaEffectComponent = world[it, SpeedAreaEffectComponent::class]
                    val bottomLeftPositionComponent = world[it, BottomLeftPositionComponent::class]
                    val sizeComponent = world[it, SizeComponent::class]
                    val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)
                    val radius = sizeComponent.toRadius()
                    val distance = currentPosition.distanceTo(centerPoint)
                    if (distance <= radius) {
                        acc * speedAreaEffectComponent.speedArea.speedEffect
                    } else {
                        acc
                    }
                }
        }
    }
}