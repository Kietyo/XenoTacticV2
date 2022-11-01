package com.xenotactic.testing

import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectionSubject<T : Any>(val actual: Collection<T>) {
    fun isEmpty() {
        assertTrue(actual.isEmpty())
    }
    fun isNotEmpty() {
        assertTrue(actual.isNotEmpty())
    }
    fun hasSize(expectedSize: Int) {
        assertEquals(expectedSize, actual.size)
    }
}