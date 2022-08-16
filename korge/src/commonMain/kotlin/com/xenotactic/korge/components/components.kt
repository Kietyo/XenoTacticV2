package com.xenotactic.korge.components

import com.soywiz.korge.view.View
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.model.MapEntityType

data class MapEntityComponent(
    val entityData: MapEntityData
)

data class UIMapEntityComponent(
    val entityView: View
)

object BlockingEntityComponent

data class SizeComponent(
    val width: Int,
    val height: Int
) {
    companion object {
        val SIZE_2X2_COMPONENT = SizeComponent(2, 2)
    }
}

// Component representing the bottom left position of an entity.
data class BottomLeftPositionComponent(
    val x: Int, val y: Int
)

data class SpeedEffectComponent(
    val speedEffect: Double
)