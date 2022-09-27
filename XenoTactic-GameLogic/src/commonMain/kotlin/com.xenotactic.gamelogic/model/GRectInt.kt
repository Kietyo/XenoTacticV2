package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit

// A game version of the rectangle.
// The (x, y) coordinate represents the bottom left of the rectangle.
// x increases from left to right
// y increases from bottom to top
data class GRectInt(
    val x: GameUnit,
    val y: GameUnit,
    val width: GameUnit,
    val height: GameUnit
) {
    val left: GameUnit
        get() = x
    val right: GameUnit
        get() = x + width
    val bottom: GameUnit
        get() = y
    val top: GameUnit
        get() = y + height
}