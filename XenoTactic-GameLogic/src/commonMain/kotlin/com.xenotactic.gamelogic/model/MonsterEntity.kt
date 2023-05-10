package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.pathing.PathSequenceTraversal
import com.xenotactic.gamelogic.utils.GameUnit

val MONSTER_WIDTH = GameUnit(1)
val MONSTER_HEIGHT = GameUnit(1)

class MonsterEntity(
    val pathSequenceTraversal: PathSequenceTraversal,
    val radiusGameUnits: Float = 0.5f,
    val movementSpeedGameUnits: Float = 20f
) {
    val currentPoint: GameUnitTuple
        get() = pathSequenceTraversal.currentPosition
}