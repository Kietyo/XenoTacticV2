package com.xenotactic.gamelogic.utils



import korlibs.math.geom.MPoint
import com.xenotactic.gamelogic.model.GameUnitTuple
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class GameUnit(val value: Double) : Comparable<GameUnit> {
    constructor(v: Int) : this(v.toDouble())

    fun toInt() = value.toInt()
    fun toDouble() = value
    fun toFloat() = value.toFloat()

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

    override operator fun compareTo(other: GameUnit): Int {
        return value.compareTo(other.value)
    }

    infix fun until(o: GameUnit): GameUnitRange {
        return GameUnitRange(this, o - 1)
    }

    operator fun times(o: Double) = GameUnit(value * o)
    operator fun times(other: Number) = GameUnit(value * other.toDouble())

    operator fun div(d: Double): GameUnit {
        return GameUnit(value / d)
    }

    operator fun div(d: GameUnit): GameUnit {
        return GameUnit(value / d.value)
    }
    operator fun div(other: Number) = GameUnit(value / other.toDouble())


    operator fun compareTo(o: Number): Int {
        return value.compareTo(o.toDouble())
    }

    fun toWorldUnit(gridSize: Number): WorldUnit {
        return WorldUnit(value * gridSize.toDouble())
    }

    infix fun tup(gridYInt: GameUnit): GameUnitTuple = GameUnitTuple(this, gridYInt)
    operator fun inc(): GameUnit = this + 1

    companion object {
        val ZERO = GameUnit(0)
    }
}

fun Number.toGameUnit(): GameUnit {
    return GameUnit(this.toDouble())
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

infix fun Int.until(o: GameUnit): GameUnitRange {
    return this.toGameUnit() until o
}

fun distance(p1: GameUnitTuple, p2: GameUnitTuple) =
    distance(p1.x, p1.y, p2.x, p2.y)

fun distance(x1: GameUnit, y1: GameUnit, x2: GameUnit, y2: GameUnit) =
    MPoint.Companion.distance(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble()).toGameUnit()

fun <T> Iterable<T>.sumOf(function: (T) -> GameUnit): GameUnit {
    var sum = GameUnit(0.0)
    forEach {
        sum += function(it)
    }
    return sum
}

@OptIn(ExperimentalStdlibApi::class)
public class GameUnitRange(
    override val start: GameUnit, override val endInclusive: GameUnit
): ClosedRange<GameUnit>, OpenEndRange<GameUnit>, Iterable<GameUnit> {
    override val endExclusive: GameUnit get() = endInclusive + 1
    override fun isEmpty(): Boolean = start < endInclusive

    override fun contains(value: GameUnit) = value >= start && value <= endInclusive
    override fun iterator(): Iterator<GameUnit> = object : Iterator<GameUnit> {
        var current = start
        var final = endInclusive
        override fun hasNext(): Boolean = current <= final
        override fun next(): GameUnit {
            return current++
        }
    }
}