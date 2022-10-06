package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.utils.distance
import com.xenotactic.gamelogic.utils.getCenterPoint
import com.xenotactic.korge.models.GameWorld
import kotlin.time.Duration

/**
 * System which adds a TargetingComponent to towers that do not yet have a targeting component.
 */
class TargetingAddSystem(
    val gameWorld: GameWorld
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            TowerComponent::class, BottomLeftPositionComponent::class, SizeComponent::class,
            RangeComponent::class
        ),
        noneOfComponents = setOf(TargetingComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getNewList().forEach { towerId ->
            val sizeComponent = gameWorld.world[towerId, SizeComponent::class]
            val bottomLeftPositionComponent = gameWorld.world[towerId, BottomLeftPositionComponent::class]
            val rangeComponent = gameWorld.world[towerId, RangeComponent::class]
            val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val nearestMonster = gameWorld.monsterFamily.getSequence().map {
                val monsterCenterPoint =
                    gameWorld.world[it, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition
                it to distance(centerPoint, monsterCenterPoint)
            }.minByOrNull {
                it.second
            }

            if (nearestMonster != null) {
                if (nearestMonster.second <= rangeComponent.range) {
                    gameWorld.world.modifyEntity(towerId) {
                        addComponentOrThrow(TargetingComponent(nearestMonster.first))
                    }
                }
            }
        }

    }
}