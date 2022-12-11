package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.components.EntityFinishComponent
import com.xenotactic.gamelogic.components.EntityStartComponent
import com.xenotactic.korge.korge_utils.StagingEntityUtils
import com.xenotactic.korge.korge_utils.intersectsEntity
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator

class CheckpointsGenerator(
    val numCheckpoints: Int
) : IGenerator {
    override fun run(context: GenerationContext) {
        //        val addedCheckpoints = mutableListOf<MapEntity.Checkpoint>()

        val startEntity = context.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityStartComponent::class
            )
        )
        val finishEntity = context.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityFinishComponent::class
            )
        )
        val addedCheckpoints = mutableListOf<StatefulEntity>()

        val size = context.getSizeOfEntity(MapEntityType.CHECKPOINT)
        for (i in 0 until numCheckpoints) {
            var position: GameUnitTuple
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
                addFromStagingEntity(
                    StagingEntityUtils.createCheckpoint(
                        i, position, size
                    )
                )
            }
            addedCheckpoints.add(addedCheckpoint)
        }


    }
}