package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.components.EntityFinishComponent
import com.xenotactic.gamelogic.components.EntityStartComponent
import com.xenotactic.gamelogic.components.EntityTypeComponent
import com.xenotactic.korge.korge_utils.StagingEntityUtils
import com.xenotactic.korge.korge_utils.intersectsEntity
import com.xenotactic.korge.korge_utils.toBottomLeftPositionComponent
import com.xenotactic.korge.korge_utils.toSizeComponent
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator

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
            addFromStagingEntity(StagingEntityUtils.createFinish(position, size))
        }
    }
}