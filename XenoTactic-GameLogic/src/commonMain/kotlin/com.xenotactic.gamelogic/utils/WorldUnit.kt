package com.xenotactic.gamelogic.utils

import com.soywiz.korma.geom.Point
import kotlin.jvm.JvmInline

typealias WorldPoint = Pair<WorldUnit, WorldUnit>

fun WorldPoint.toPoint() = Point(first.toDouble(), second.toDouble())

@JvmInline
value class WorldUnit(val value: Double) {
    operator fun div(i: Int): WorldUnit = WorldUnit(value / i)
    operator fun minus(borderSize: Double) = WorldUnit(value - borderSize)
    operator fun minus(other: WorldUnit) = WorldUnit(value - other.value)
    fun toDouble(): Double = value

    companion object {
        operator fun invoke(v: GameUnit) = WorldUnit(v.toDouble())
    }
}

fun Number.toWorldUnit() = WorldUnit(this.toDouble())