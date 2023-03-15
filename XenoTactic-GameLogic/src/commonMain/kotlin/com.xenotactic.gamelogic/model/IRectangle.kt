package com.xenotactic.gamelogic.model

data class IRectangle(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
) {
    fun contains(p: IPoint): Boolean {
        return p.x in x..(x + width) && y in y..(y + height)
    }
}
