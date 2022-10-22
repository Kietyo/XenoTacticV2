package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.korge_utils.xy
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class MonsterMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class, UIEntityViewComponent::class,
            PathSequenceTraversalComponent::class,
            VelocityComponent::class,
        )
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val traversal = world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal
            val currentPoint = traversal.currentPosition
            val uiEntityViewComponent = world[it, UIEntityViewComponent::class]
            val movementVelocityComponent = world[it, VelocityComponent::class]
            val computedSpeedEffectComponent = world[it, ComputedSpeedEffectComponent::class]

            val (worldX, worldY) = uiMapV2.getWorldCoordinates(
                currentPoint.x, currentPoint.y,
                0.toGameUnit()
            )

            uiEntityViewComponent.entityView.xy(worldX, worldY)

            traversal.traverse(movementVelocityComponent.velocity * computedSpeedEffectComponent.computedSpeedEffect)
        }
    }
}