package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.utils.distance
import com.xenotactic.gamelogic.utils.getCenterPoint
import kotlin.time.Duration

class TowerTargetingRemoveSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            EntityTowerComponent::class,
            BottomLeftPositionComponent::class,
            SizeComponent::class,
            RangeComponent::class,
            TargetingComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getNewList().forEach {
            val targetingComponent = world[it, TargetingComponent::class]

            // Monster was already removed.
            if (!world.containsEntity(targetingComponent.targetEntityId)) {
                world.modifyEntity(it) {
                    removeComponent<TargetingComponent>()
                }
                return@forEach
            }

            val rangeComponent = world[it, RangeComponent::class]
            val sizeComponent = world[it, SizeComponent::class]
            val bottomLeftPositionComponent = world[it, BottomLeftPositionComponent::class]
            val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val monsterCenterPoint = world[targetingComponent.targetEntityId, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

            if (distance(centerPoint, monsterCenterPoint) > rangeComponent.range) {
                world.modifyEntity(it) {
                    removeComponent<TargetingComponent>()
                }
            }
        }
    }
}