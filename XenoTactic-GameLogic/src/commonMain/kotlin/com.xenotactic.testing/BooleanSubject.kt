package com.xenotactic.testing

import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BooleanSubject(val actual: Boolean) {
    fun isTrue() {
        assertTrue(actual)
    }
    fun isFalse() {
        assertFalse(actual)
    }
}