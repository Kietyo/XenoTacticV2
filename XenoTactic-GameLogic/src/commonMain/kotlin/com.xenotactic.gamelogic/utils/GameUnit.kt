package com.xenotactic.gamelogic.utils

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class GameUnit(val value: Int) {
    operator fun plus(other: GameUnit): GameUnit {
        return GameUnit(value + other.value)
    }

    operator fun plus(other: Int): GameUnit {
        return GameUnit(value + other)
    }

    operator fun minus(y: GameUnit): GameUnit {
        return GameUnit(value - y.value)
    }

    operator fun minus(y: Int): GameUnit {
        return GameUnit(value - y)
    }

    operator fun times(i: GameUnit): GameUnit {
        return GameUnit(value * i.value)
    }
    operator fun times(i: Int): GameUnit {
        return GameUnit(value * i)
    }

    operator fun compareTo(i: Int): Int {
        return value.compareTo(i)
    }

    operator fun compareTo(i: GameUnit): Int {
        return value.compareTo(i.value)
    }

    infix fun until(o: GameUnit): IntRange {
        return value until o.value
    }

}

fun Int.toGameUnit(): GameUnit = GameUnit(this)
fun Double.toGameUnit(): GameUnit {
    return GameUnit(this.toInt())
}
fun max(a: GameUnit, b: Int): GameUnit {
    return GameUnit(kotlin.math.max(a.value, b))
}
fun min(width: GameUnit, gameUnit: GameUnit): GameUnit {
    return GameUnit(kotlin.math.min(width.value, gameUnit.value))
}
operator fun Int.rangeTo(o: GameUnit): IntRange {
    return this..o.value
}

