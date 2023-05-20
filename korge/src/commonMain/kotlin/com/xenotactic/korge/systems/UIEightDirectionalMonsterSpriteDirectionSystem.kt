package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.utils.getDirection8
import kotlin.time.Duration

class UIEightDirectionalMonsterSpriteDirectionSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.UIEightDirectionalSpriteComponent::class,
            com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { entityId ->
            val uiEightDirectionalSpriteComponent =
                world[entityId, com.xenotactic.gamelogic.components.UIEightDirectionalSpriteComponent::class]
            val pathSequenceTraversalComponent =
                world[entityId, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class]

            val angle = pathSequenceTraversalComponent.pathSequenceTraversal.let {
                it.currentPosition.toPoint().angleTo(it.currentDestinationPoint.toPoint())
            }
            val direction8 = getDirection8(angle)
            uiEightDirectionalSpriteComponent.uiEightDirectionalSprite.changeToDirection(direction8)
        }

    }
}