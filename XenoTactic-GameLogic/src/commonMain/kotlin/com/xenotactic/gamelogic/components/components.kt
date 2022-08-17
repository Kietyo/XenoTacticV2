package com.xenotactic.gamelogic.components

import com.soywiz.korge.view.Text
import com.soywiz.korge.view.View
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.views.UIEntity

data class MapEntityComponent(
    val entityData: MapEntityData
)

data class UIMapEntityComponent(
    val entityView: UIEntity
)

data class UIMapEntityTextComponent(
    val textView: Text
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
