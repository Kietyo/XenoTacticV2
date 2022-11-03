package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.model.GameUnitPoint
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.components.EntityCheckpointComponent
import com.xenotactic.korge.components.EntityFinishComponent
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.korge.korge_utils.intersectsEntity
import com.xenotactic.korge.korge_utils.toBottomLeftPositionComponent
import com.xenotactic.korge.korge_utils.toSizeComponent
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator

class CheckpointsGenerator(
    val numCheckpoints: Int
) : IGenerator {
    override fun run(context: GenerationContext) {
        //        val addedCheckpoints = mutableListOf<MapEntity.Checkpoint>()

        val startEntity = context.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityStartComponent::class
            )
        )
        val finishEntity = context.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                EntityFinishComponent::class
            )
        )
        val addedCheckpoints = mutableListOf<StatefulEntity>()

        val size = context.getSizeOfEntity(MapEntityType.CHECKPOINT)
        for (i in 0 until numCheckpoints) {
            var position: GameUnitPoint
            do {
                context.incrementNumAttempts {
                    "Failed to create place CHECKPOINT $i."
                }
                position = context.getRandomPointWithinMapBounds(size)
            } while (
                startEntity.intersectsEntity(position, size) ||
                finishEntity.intersectsEntity(position, size) ||
                addedCheckpoints.any { it.intersectsEntity(position, size) }
            )
            val addedCheckpoint = context.world.addEntityReturnStateful {
                addComponentOrThrow(size.toSizeComponent())
                addComponentOrThrow(position.toBottomLeftPositionComponent())
                addComponentOrThrow(EntityCheckpointComponent(i))
            }
            addedCheckpoints.add(addedCheckpoint)
        }


    }
}