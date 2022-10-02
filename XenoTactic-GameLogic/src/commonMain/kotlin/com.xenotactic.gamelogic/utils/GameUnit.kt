package com.xenotactic.gamelogic.utils

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class GameUnit(val value: Double) {
    constructor(v: Int) : this(v.toDouble())
    fun toInt() = value.toInt()
    fun toDouble() = value
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
        return value.compareTo(i.toDouble())
    }

    operator fun compareTo(i: GameUnit): Int {
        return value.compareTo(i.value)
    }

    infix fun until(o: GameUnit): IntRange {
        return value.toInt() until o.value.toInt()
    }

    operator fun times(o: Double): GameUnit {
        return GameUnit(value * o)
    }

}

fun Int.toGameUnit(): GameUnit = GameUnit(this.toDouble())
fun Double.toGameUnit(): GameUnit {
    return GameUnit(this)
}
fun max(a: GameUnit, b: Int): GameUnit {
    return GameUnit(kotlin.math.max(a.value, b.toDouble()))
}
fun min(width: GameUnit, gameUnit: GameUnit): GameUnit {
    return GameUnit(kotlin.math.min(width.value, gameUnit.value))
}
operator fun Int.rangeTo(o: GameUnit): IntRange {
    return this..o.value.toInt()
}
infix fun Int.until(o: GameUnit): IntRange {
    return this until o.value.toInt()
}


