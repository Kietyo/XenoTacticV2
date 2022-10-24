package com.xenotactic.testing

import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.test.assertEquals

class DoubleSubject(val actual: Double) {
    fun almostEqualsTo(e: Double, absoluteTolerance: Double = 0.00001) {
        assertEquals(e, actual, absoluteTolerance)
    }
}

fun assertThat(v: Double) = DoubleSubject(v)
fun assertThat(v: GameUnit) = DoubleSubject(v.toDouble())