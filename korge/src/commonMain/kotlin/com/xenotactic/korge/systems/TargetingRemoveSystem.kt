package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.utils.distance
import com.xenotactic.korge.components.*
import com.xenotactic.korge.korge_utils.getCenterPoint
import kotlin.time.Duration

class TargetingRemoveSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            TowerComponent::class, BottomLeftPositionComponent::class, SizeComponent::class,
            RangeComponent::class, TargetingComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val targetingComponent = world[it, TargetingComponent::class]

            // Monster was already removed.
            if (!world.containsEntity(targetingComponent.targetEntityId)) {
                world.modifyEntity(it) {
                    removeComponent<TargetingComponent>()
                }
                return
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