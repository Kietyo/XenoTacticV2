package com.xenotactic.testing

import kotlin.test.*

internal class DoubleSubjectTest {
    @Test
    fun assertTest1() {
        testAssertFailsWithMessage("""
            absolute difference: ${0.00001}
            expected:<3.0> but was:<2.0>
        """.trimIndent()) {
            assertThat(2.0).isEqualTo(3.0)
        }
    }

    @Test
    fun assertTest2() {
        assertThat(2.0).isEqualTo(2.0)
    }
}