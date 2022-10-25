package com.xenotactic.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse

internal class DoubleSubjectTest {
    @Test
    fun assertTest() {
        assertFails {
            assertThat(2.0).isEqualTo(3.0)
        }

    }
}