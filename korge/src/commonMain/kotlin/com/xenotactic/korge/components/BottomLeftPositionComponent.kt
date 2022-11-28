package com.xenotactic.korge.components

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

// Component representing the bottom left position of an entity.
data class BottomLeftPositionComponent(
    val x: GameUnit, val y: GameUnit
) {
    fun toTuple() = GameUnitTuple(x, y)

    constructor(x: Int, y: Int): this(x.toGameUnit(), y.toGameUnit())
}