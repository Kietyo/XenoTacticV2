package com.xenotactic.testing

import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.test.assertEquals
import kotlin.test.assertSame

fun assertThat(v: Double) = DoubleSubject(v)
fun assertThat(v: GameUnit) = DoubleSubject(v.toDouble())

fun testAssertFailsWithMessage(expectedErrorMessage: String, block: () -> Unit) {
    try {
        block()
    } catch (assertionError: AssertionError) {
        assertEquals(expectedErrorMessage, assertionError.message)
    }
}