package com.xenotactic.gamelogic.utils

import korlibs.math.geom.Point
import kotlin.jvm.JvmInline

typealias WorldPoint = Pair<WorldUnit, WorldUnit>

fun WorldPoint.toPoint() = Point(first.toDouble(), second.toDouble())

@JvmInline
value class WorldUnit(val value: Double) {
    operator fun div(i: Int): WorldUnit = WorldUnit(value / i)
    operator fun minus(other: Double) = WorldUnit(value - other)
    operator fun minus(other: Float) = WorldUnit(value - other)
    operator fun minus(other: Number) = WorldUnit(value - other.toDouble())
    operator fun minus(other: WorldUnit) = WorldUnit(value - other.value)
    fun toDouble(): Double = value
    fun toFloat() = value.toFloat()
    operator fun plus(other: WorldUnit) = WorldUnit(value + other.value)

    companion object {
        val ZERO = WorldUnit(0.0)

        operator fun invoke(v: GameUnit) = WorldUnit(v.toDouble())
    }
}

fun Number.toWorldUnit() = WorldUnit(this.toDouble())