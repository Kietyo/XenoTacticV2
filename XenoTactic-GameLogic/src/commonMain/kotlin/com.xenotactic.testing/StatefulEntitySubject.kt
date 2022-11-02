package com.xenotactic.testing

import com.xenotactic.ecs.StatefulEntity
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StatefulEntitySubject(val actual: StatefulEntity) {
    fun containsComponents(vararg klass: KClass<out Any>) {
        assertTrue(actual.containsComponentTypes(*klass))
    }

    fun containsExactlyComponentTypes(vararg klass: KClass<out Any>) {
        val asSet = klass.toSet()
        require(klass.size == asSet.size) {
            "You provide duplicate components!"
        }
        assertEquals(asSet.size, actual.numComponents)
        assertTrue(actual.containsComponentTypes(*klass))
    }

    fun containsExactlyComponents(vararg elements: Any) {
        assertEquals(elements.size, actual.numComponents)
        elements.forEach {
            assertEquals(it, actual[it::class])
        }
    }
}