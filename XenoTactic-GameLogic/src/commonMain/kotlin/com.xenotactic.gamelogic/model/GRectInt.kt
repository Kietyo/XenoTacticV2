package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

// A game version of the rectangle.
// The (x, y) coordinate represents the bottom left of the rectangle.
// x increases from left to right
// y increases from bottom to top
data class GRectInt(
    override val x: GameUnit,
    override val y: GameUnit,
    override val width: GameUnit,
    override val height: GameUnit
): IRectangleEntity {
    companion object {
        operator fun invoke(x: Int, y: Int, width: Int, height: Int) = GRectInt(
            x.toGameUnit(),
            y.toGameUnit(),
            width.toGameUnit(),
            height.toGameUnit()
        )
    }
}