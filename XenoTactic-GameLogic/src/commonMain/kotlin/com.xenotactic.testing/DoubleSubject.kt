package com.xenotactic.testing

import kotlin.math.abs

private fun checkAbsoluteTolerance(absoluteTolerance: Double) {
    require(absoluteTolerance >= 0.0) { "Illegal negative absolute tolerance <$absoluteTolerance>." }
    require(!absoluteTolerance.isNaN()) { "Illegal NaN absolute tolerance <$absoluteTolerance>." }
}

internal fun checkDoublesAreEqual(
    expected: Double,
    actual: Double,
    absoluteTolerance: Double,
    shouldFail: Boolean = false
) {
    checkAbsoluteTolerance(absoluteTolerance)
    val equal = expected.toBits() == actual.toBits() || abs(expected - actual) <= absoluteTolerance

    if (shouldFail && !equal) {
        println("absolute difference: $absoluteTolerance")
        throw AssertionError("expected:<$expected> but was:<$actual>")
    }
}

class DoubleSubject(val actual: Double) {
    fun isEqualTo(e: Double, absoluteTolerance: Double = 0.00001) {
//        assertEquals(e, actual, absoluteTolerance)
        checkDoublesAreEqual(e, actual, absoluteTolerance, shouldFail = true)
    }
}