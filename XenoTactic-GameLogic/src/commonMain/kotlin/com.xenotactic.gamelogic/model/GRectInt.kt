package com.xenotactic.gamelogic.model

// A game version of the rectangle.
// The (x, y) coordinate represents the bottom left of the rectangle.
// x increases from left to right
// y increases from bottom to top
data class GRectInt(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    val left: Int
        get() = x
    val right: Int
        get() = x + width
    val bottom: Int
        get() = y
    val top: Int
        get() = y + height
}