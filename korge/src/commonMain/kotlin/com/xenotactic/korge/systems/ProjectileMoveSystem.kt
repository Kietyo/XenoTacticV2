package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.pathing.Segment
import pathing.SegmentTraversal
import kotlin.time.Duration

class ProjectileMoveSystem(val world: World) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.ProjectileComponent::class, com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class,
            com.xenotactic.gamelogic.components.TargetingComponent::class, com.xenotactic.gamelogic.components.VelocityComponent::class
        ),
        noneOfComponents = setOf(com.xenotactic.gamelogic.components.CollideWithTargetComponent::class)
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val mutableCenterPositionComponent = world[it, com.xenotactic.gamelogic.components.MutableCenterPositionComponent::class]
            val targetingComponent = world[it, com.xenotactic.gamelogic.components.TargetingComponent::class]
            val velocityComponent = world[it, com.xenotactic.gamelogic.components.VelocityComponent::class]
            val monsterCenterPoint =
                world[targetingComponent.targetEntityId, com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class].pathSequenceTraversal.currentPosition

            val segmentTraversal = SegmentTraversal(
                Segment(
                    GameUnitTuple(mutableCenterPositionComponent.x, mutableCenterPositionComponent.y),
                    monsterCenterPoint
                )
            )

            segmentTraversal.traverse(velocityComponent.velocity)

            mutableCenterPositionComponent.x = segmentTraversal.currentPosition.x
            mutableCenterPositionComponent.y = segmentTraversal.currentPosition.y

            if (segmentTraversal.finishedTraversal()) {
                world.modifyEntity(it) {
                    addComponentOrThrow(com.xenotactic.gamelogic.components.CollideWithTargetComponent)
                }
            }

        }
    }
}