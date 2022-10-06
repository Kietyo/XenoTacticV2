package com.xenotactic.gamelogic.utils

import kotlin.jvm.JvmInline

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