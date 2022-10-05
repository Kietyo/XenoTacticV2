package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.utils.GameUnit
import pathing.PathSequenceTraversal

val MONSTER_WIDTH = GameUnit(1)
val MONSTER_HEIGHT = GameUnit(1)

class MonsterEntity(
    val pathSequenceTraversal: PathSequenceTraversal,
    val radiusGameUnits: Float = 0.5f,
    val movementSpeedGameUnits: Float = 20f
) {
    val currentPoint: GameUnitPoint
        get() = pathSequenceTraversal.currentPosition
}