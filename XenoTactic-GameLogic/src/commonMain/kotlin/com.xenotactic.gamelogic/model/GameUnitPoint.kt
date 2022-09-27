package com.xenotactic.gamelogic.model

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.Serializable

@Serializable
data class GameUnitPoint(val x: GameUnit, val y: GameUnit) {

    constructor(x: Int, y: Int) : this(x.toGameUnit(), y.toGameUnit())

    companion object {
        val ZERO = GameUnitPoint(0.toGameUnit(), 0.toGameUnit())
    }

    fun toPoint(): Point {
        return Point(x.value, y.value)
    }
}