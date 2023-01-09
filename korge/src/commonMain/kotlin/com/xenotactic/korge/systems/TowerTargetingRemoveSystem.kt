package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.distance
import com.xenotactic.korge.korge_utils.getCenterPoint
import kotlin.time.Duration

class TowerTargetingRemoveSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.EntityTowerComponent::class, com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class, com.xenotactic.gamelogic.components.SizeComponent::class,
            com.xenotactic.gamelogic.components.RangeComponent::class, com.xenotactic.gamelogic.components.TargetingComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getNewList().forEach {
            val targetingComponent = world[it, com.xenotactic.gamelogic.components.TargetingComponent::class]

            // Monster was already removed.
            if (!world.containsEntity(targetingComponent.targetEntityId)) {
                world.modifyEntity(it) {
                    removeComponent<com.xenotactic.gamelogic.components.TargetingComponent>()
                }
                return@forEach
            }

            val rangeComponent = world[it, com.xenotactic.gamelogic.components.RangeComponent::class]
            val sizeComponent = world[it, com.xenotactic.gamelogic.components.SizeComponent::class]
            val bottomLeftPositionComponent = world[it, com.xenotactic.gamelogic.components.BottomLeftPositionComponent::class]
            val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val monsterCenterPoint = world[targetingComponent.targetEntityId, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

            if (distance(centerPoint, monsterCenterPoint) > rangeComponent.range) {
                world.modifyEntity(it) {
                    removeComponent<com.xenotactic.gamelogic.components.TargetingComponent>()
                }
            }
        }
    }
}