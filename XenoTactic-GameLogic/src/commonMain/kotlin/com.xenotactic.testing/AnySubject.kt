package com.xenotactic.testing

import kotlin.test.assertEquals

class AnySubject<T : Any>(val actual: T) {
    fun isEqualTo(expected: T) {
        assertEquals(expected, actual)
    }
}