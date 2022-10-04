package com.xenotactic.gamelogic.components

import com.soywiz.korge.view.Text
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.views.UIEntity
import pathing.PathSequenceTraversal

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

object MonsterComponent
data class SizeComponent(
    val width: GameUnit,
    val height: GameUnit
) {
    companion object {
        val SIZE_2X2_COMPONENT = SizeComponent(2.toGameUnit(), 2.toGameUnit())
    }
}

// Component representing the bottom left position of an entity.
data class BottomLeftPositionComponent(
    val x: GameUnit, val y: GameUnit
)

data class PathSequenceTraversalComponent(
    val pathSequenceTraversal: PathSequenceTraversal
)

data class MovementSpeedComponent(
    val movementSpeed: Double = 0.1
)