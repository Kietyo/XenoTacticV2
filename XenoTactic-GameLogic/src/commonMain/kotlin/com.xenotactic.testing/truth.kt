package com.xenotactic.testing

import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.test.assertEquals

fun assertThat(actual: Double) = DoubleSubject(actual)
fun assertThat(actual: GameUnit) = DoubleSubject(actual.toDouble())
fun <T: Any> assertThat(actual: T) = AnySubject(actual)
fun <T: Any> assertThat(actual: Collection<T>) = CollectionSubject(actual)

fun testAssertFailsWithMessage(expectedErrorMessage: String, block: () -> Unit) {
    try {
        block()
    } catch (assertionError: AssertionError) {
        assertEquals(expectedErrorMessage, assertionError.message)
    }
}