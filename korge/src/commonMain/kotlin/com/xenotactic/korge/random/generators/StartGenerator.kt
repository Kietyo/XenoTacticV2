package com.xenotactic.korge.random.generators

import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.components.EntityStartComponent
import com.xenotactic.korge.korge_utils.toBottomLeftPositionComponent
import com.xenotactic.korge.korge_utils.toSizeComponent
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator

object StartGenerator : IGenerator {
    override fun run(context: GenerationContext) {
        val size = context.getSizeOfEntity(MapEntityType.START)
        val point = context.getRandomPointWithinMapBounds(size)

        context.world.addEntity {
            addComponentOrThrow(size.toSizeComponent())
            addComponentOrThrow(point.toBottomLeftPositionComponent())
            addComponentOrThrow(EntityStartComponent)
        }
    }
}