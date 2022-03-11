package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Point
import pathing.PathSequenceTraversal

const val MONSTER_WIDTH = 1
const val MONSTER_HEIGHT = 1

class MonsterEntity(
    val pathSequenceTraversal: PathSequenceTraversal,
    val radiusGameUnits: Float = 0.5f,
    val movementSpeedGameUnits: Float = 20f
) {
    val currentPoint: Point
        get() = pathSequenceTraversal.currentPosition
}