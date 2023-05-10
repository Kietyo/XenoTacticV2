package com.xenotactic.gamelogic.model

import korlibs.math.geom.Angle
import kotlin.math.sqrt

data class IPoint(val x: Double, val y: Double) {

    constructor(x: Number, y: Number): this(x.toDouble(), y.toDouble())

    fun distanceTo(other: IPoint): Double {
        return sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
    }

    fun angleTo(other: IPoint): Angle = Angle.between(this.x, this.y, other.x, other.y)
    operator fun plus(other: IPoint) = IPoint(x + other.x, y + other.y)

    companion object {
        val ZERO = IPoint(0.0, 0.0)
    }
}
