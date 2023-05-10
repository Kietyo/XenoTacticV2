package com.xenotactic.gamelogic.model

data class IRectangle(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
) {
    constructor(x: Number, y: Number, width: Number, height: Number):
            this(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    fun contains(p: IPoint): Boolean {
        return p.x in x..(x + width) && p.y in y..(y + height)
    }
}
