package com.xenotactic.korge.random.generators

import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import com.xenotactic.korge.utils.StagingEntityUtils

object StartGenerator : IGenerator {
    override fun run(context: GenerationContext) {
        val size = context.getSizeOfEntity(MapEntityType.START)
        val point = context.getRandomPointWithinMapBounds(size)

        context.world.addEntity {
            addComponentsFromStagingEntity(
                StagingEntityUtils.createStart(
                    point, size
                )
            )
        }
    }
}