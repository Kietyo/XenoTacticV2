package com.xenotactic.testing

import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.test.assertEquals

fun assertThat(actual: Double) = DoubleSubject(actual)
fun assertThat(actual: GameUnit) = DoubleSubject(actual.toDouble())

fun assertThat(actual: PathFindingResult) = PathFindingResultSubject(actual)

fun testAssertFailsWithMessage(expectedErrorMessage: String, block: () -> Unit) {
    try {
        block()
    } catch (assertionError: AssertionError) {
        assertEquals(expectedErrorMessage, assertionError.message)
    }
}