package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.MonsterComponent
import com.xenotactic.korge.components.MovementSpeedComponent
import com.xenotactic.korge.components.PathSequenceTraversalComponent
import com.xenotactic.korge.components.UIMapEntityComponent
import com.xenotactic.gamelogic.korge_utils.xy
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.ui.UIMapV2
import kotlin.time.Duration

class MonsterMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MonsterComponent::class, UIMapEntityComponent::class,
            PathSequenceTraversalComponent::class,
            MovementSpeedComponent::class
        )
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val traversal = world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal
            val currentPoint = traversal.currentPosition
            val uiMapEntityComponent = world[it, UIMapEntityComponent::class]
            val movementSpeedComponent = world[it, MovementSpeedComponent::class]

            val (worldX, worldY) = uiMapV2.getWorldCoordinates(
                currentPoint.x, currentPoint.y,
                0.toGameUnit()
            )

            uiMapEntityComponent.entityView.xy(worldX, worldY)

            traversal.traverse(movementSpeedComponent.movementSpeed)
        }
    }
}