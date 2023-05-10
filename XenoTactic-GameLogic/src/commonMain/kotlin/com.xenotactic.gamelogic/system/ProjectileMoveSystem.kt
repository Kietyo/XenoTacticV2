package com.xenotactic.gamelogic.system

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.pathing.Segment
import com.xenotactic.gamelogic.pathing.SegmentTraversal
import kotlin.time.Duration

class ProjectileMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            ProjectileComponent::class, MutableCenterPositionComponent::class,
            TargetingComponent::class, VelocityComponent::class
        ),
        noneOfComponents = setOf(CollideWithTargetComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val mutableCenterPositionComponent = world[it, MutableCenterPositionComponent::class]
            val targetingComponent = world[it, TargetingComponent::class]
            val velocityComponent = world[it, VelocityComponent::class]
            val monsterCenterPoint =
                world[targetingComponent.targetEntityId, PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

            val segmentTraversal = SegmentTraversal(
                Segment(
                    GameUnitTuple(mutableCenterPositionComponent.x, mutableCenterPositionComponent.y),
                    monsterCenterPoint
                )
            )

            segmentTraversal.traverse(velocityComponent.calculateDistance(deltaTime))

            mutableCenterPositionComponent.x = segmentTraversal.currentPosition.x
            mutableCenterPositionComponent.y = segmentTraversal.currentPosition.y

            if (segmentTraversal.finishedTraversal()) {
                world.modifyEntity(it) {
                    addComponentOrThrow(CollideWithTargetComponent)
                }
            }

        }
    }
}