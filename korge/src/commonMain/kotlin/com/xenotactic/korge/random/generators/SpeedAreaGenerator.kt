package com.xenotactic.korge.random.generators

import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.random.GenerationContext
import com.xenotactic.korge.random.IGenerator
import com.xenotactic.korge.utils.StagingEntityUtils

class SpeedAreaGenerator(
    val numSpeedAreas: Int
) : IGenerator {
    override fun run(context: GenerationContext) {
        repeat(numSpeedAreas) {
            val radius = context.random.nextInt(1, 11)
            val diameter = (radius * 2).toGameUnit()
            val speedEffect = context.random.nextDouble(0.25, 1.90)

            val position = context.getRandomPointPartiallyInMap(diameter, diameter)
            context.world.addEntity {
                addComponentsFromStagingEntity(
                    StagingEntityUtils.createSpeedArea(
                        position, diameter, speedEffect
                    )
                )
            }
        }
    }
}