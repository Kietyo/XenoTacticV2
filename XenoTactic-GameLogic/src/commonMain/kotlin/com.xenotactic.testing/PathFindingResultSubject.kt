package com.xenotactic.testing

import com.xenotactic.gamelogic.pathing.PathFindingResult
import kotlin.test.assertEquals

class PathFindingResultSubject(val actual: PathFindingResult) {
    fun isEqualTo(expected: PathFindingResult) {
        assertEquals(expected, actual)
    }

}
