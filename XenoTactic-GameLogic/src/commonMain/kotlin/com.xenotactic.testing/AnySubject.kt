package com.xenotactic.testing

import kotlin.test.assertEquals
import kotlin.test.assertIs

class AnySubject<T : Any>(val actual: T) {
    fun isEqualTo(expected: T) {
        assertEquals(expected, actual)
    }

    inline fun <reified E> isInstanceOf() {
        assertIs<E>(actual)
    }
}