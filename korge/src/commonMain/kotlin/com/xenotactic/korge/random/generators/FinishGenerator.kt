package com.xenotactic.korge.random.generators

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.components.EntityFinishComponent
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.korge.components.EntityTypeComponent
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
                EntityStartComponent::class
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