package com.xenotactic.gamelogic.model

import korlibs.math.geom.Angle

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.distance
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.Serializable

@Serializable
data class GameUnitTuple(val x: GameUnit, val y: GameUnit) {
    val first get() = x
    val second get() = y
    companion object {
        val ZERO = GameUnitTuple(0.toGameUnit(), 0.toGameUnit())

        operator fun invoke(x: Int, y: Int): GameUnitTuple {
            return GameUnitTuple(x.toGameUnit(), y.toGameUnit())
        }
        operator fun invoke(x: Float, y: Float): GameUnitTuple {
            return GameUnitTuple(x.toGameUnit(), y.toGameUnit())
        }
        operator fun invoke(x: Double, y: Double): GameUnitTuple {
            return GameUnitTuple(x.toGameUnit(), y.toGameUnit())
        }

    }

    fun toPoint(): IPoint {
        return IPoint(x.value, y.value)
    }

    fun distanceTo(point2: GameUnitTuple): GameUnit {
        return distance(x, y, point2.x, point2.y)
    }

    fun angleTo(other: GameUnitTuple): Angle {
        return Angle.between(x.toDouble(), y.toDouble(), other.x.toDouble(), other.y.toDouble())
    }
}

fun IPoint.toGameUnitPoint(): GameUnitTuple {
    return GameUnitTuple(x.toGameUnit(), y.toGameUnit())
}

infix fun Number.tup(i: Number): GameUnitTuple = GameUnitTuple(this.toDouble(), i.toDouble())

