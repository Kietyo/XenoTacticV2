package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import com.xenotactic.korge.utils.StagingEntityUtils
import com.xenotactic.korge.utils.intersectsEntity

object FinishGenerator : IGenerator {
    override fun run(context: GenerationContext) {
        val startEntity = context.world.getFirstStatefulEntityMatching(
            FamilyConfiguration.allOf(
                com.xenotactic.gamelogic.components.EntityStartComponent::class
            )
        )
        val size = context.getSizeOfEntity(MapEntityType.FINISH)
        var position: GameUnitTuple
        do {
            context.incrementNumAttempts {
                "Failed to create FINISH entity in a spot that didn't intersect with START."
            }
            position = context.getRandomPointWithinMapBounds(size)
        } while (startEntity.intersectsEntity(position, size))

        context.world.addEntity {
            addComponentsFromStagingEntity(StagingEntityUtils.createFinish(position, size))
        }
    }
}