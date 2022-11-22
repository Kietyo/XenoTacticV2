package com.xenotactic.korge.random.generators

import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.EntitySpeedAreaComponent
import com.xenotactic.korge.components.EntityTypeComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.korge.korge_utils.toBottomLeftPositionComponent
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator

class SpeedAreaGenerator(
    val numSpeedAreas: Int
) : IGenerator {
    override fun run(context: GenerationContext) {
        repeat(numSpeedAreas) {
            val radius = context.random.nextInt(1, 11)
            val diameter = radius * 2
            val speedEffect = context.random.nextDouble(0.25, 1.90)

            val position = context.getRandomPointPartiallyInMap(diameter, diameter)
            context.world.addEntity {
                addComponentOrThrow(position.toBottomLeftPositionComponent())
                addComponentOrThrow(SizeComponent(diameter, diameter))
                addComponentOrThrow(EntitySpeedAreaComponent(speedEffect))
                addComponentOrThrow(EntityTypeComponent(MapEntityType.SPEED_AREA))
            }
        }
    }
}