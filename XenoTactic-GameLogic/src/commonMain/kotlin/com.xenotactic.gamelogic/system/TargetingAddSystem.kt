package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.utils.distance
import com.xenotactic.gamelogic.utils.getCenterPoint
import kotlin.time.Duration

/**
 * System which adds a TargetingComponent to towers that do not yet have a targeting component.
 */
class TargetingAddSystem(
    val gameWorld: GameWorld
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.EntityTowerComponent::class,
            com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class,
            com.xenotactic.gamelogic.components.SizeComponent::class,
            com.xenotactic.gamelogic.components.RangeComponent::class
        ),
        noneOfComponents = setOf(com.xenotactic.gamelogic.components.TargetingComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { towerId ->
            val sizeComponent = gameWorld.world[towerId, com.xenotactic.gamelogic.components.SizeComponent::class]
            val bottomLeftPositionComponent = gameWorld.world[towerId, com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class]
            val rangeComponent = gameWorld.world[towerId, com.xenotactic.gamelogic.components.RangeComponent::class]
            val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val nearestMonster = gameWorld.monsterFamily.getSequence().map {
                val monsterCenterPoint =
                    gameWorld.world[it, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition
                it to distance(centerPoint, monsterCenterPoint)
            }.minByOrNull {
                it.second
            }

            if (nearestMonster != null) {
                if (nearestMonster.second <= rangeComponent.range) {
                    gameWorld.world.modifyEntity(towerId) {
                        addComponentOrThrow(com.xenotactic.gamelogic.components.TargetingComponent(nearestMonster.first))
                    }
                }
            }
        }

    }
}