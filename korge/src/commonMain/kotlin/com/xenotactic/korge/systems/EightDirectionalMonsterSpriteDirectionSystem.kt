package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.*
import com.xenotactic.korge.korge_utils.getDirection8
import com.xenotactic.korge.korge_utils.kAngleTo
import kotlin.time.Duration

class EightDirectionalMonsterSpriteDirectionSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            UIEightDirectionalSpriteComponent::class,
            PathSequenceTraversalComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { entityId ->
            val uiEightDirectionalSpriteComponent = world[entityId, UIEightDirectionalSpriteComponent::class]
            val pathSequenceTraversalComponent = world[entityId, PathSequenceTraversalComponent::class]

            val angle = pathSequenceTraversalComponent.pathSequenceTraversal.let {
                it.currentPosition.toPoint().angleTo(it.currentDestinationPoint.toPoint())
            }
            val direction8 = getDirection8(angle)
            uiEightDirectionalSpriteComponent.uiEightDirectionalSprite.changeToDirection(direction8)
        }

    }
}