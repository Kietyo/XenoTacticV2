package test_utils

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.CircleIntersectionUtil

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

const val TEST_FLOAT_MAX_DELTA = 0.00001f
const val TEST_DOUBLE_MAX_DELTA = 0.00001

fun assertPointSetEquals(expect: Set<Point>, actual: Set<Point>) {
    assertEquals(expect.size, actual.size)
    for (p1 in expect) {
        var found = false
        for (p2 in actual) {
            if (pointEquals(p1, p2)) {
                found = true
                break
            }
        }
        assertTrue(
            found, message =
            """
                expect: $expect,
                actual: $actual,
                Missing point p1 ($p1) in expect.
            """.trimIndent()
        )
    }
}

fun assertFloatEquals(expected: Float, actual: Float) {
    assertEquals(expected, actual, TEST_FLOAT_MAX_DELTA)
}

fun assertFloatEquals(expected: Float, actual: Double) {
    assertEquals(expected.toDouble(), actual, TEST_DOUBLE_MAX_DELTA)
}

fun assertDoubleEquals(expected: Double, actual: Double) {
    assertEquals(expected, actual, TEST_DOUBLE_MAX_DELTA)
}

fun assertPointEquals(expected: Point, actual: Point) {
    assertTrue(
        pointEquals(expected, actual), message =
        """Points are not equal.
        expected: $expected        ,
        actual: $actual
    """.trimIndent()
    )
}

fun pointEquals(actual: Point, expected: Point): Boolean {
    return doubleEquals(actual.x, expected.x) && doubleEquals(actual.y, expected.y)
}

fun doubleEquals(d1: Double, d2: Double, threshold: Double = TEST_DOUBLE_MAX_DELTA): Boolean {
    return (d1 - d2).absoluteValue <= threshold
}

fun assertPathEquals(expected: Path, actual: Path) {
    assertEquals(expected.numPoints, actual.numPoints)
    for (i in 0 until expected.numPoints) {
        assertPointEquals(expected.points[i], actual.points[i])
    }
}

fun assertPathSequenceEquals(expected: PathSequence?, actual: PathSequence?) {
    if (expected == null && actual == null) return

    if (expected != null && actual != null) {
        assertEquals(expected.numPaths, actual.numPaths)
        assertEquals(expected.paths, actual.paths)
        //        for (i in 0 until expected.numPaths) {
        //            assertPathEquals(expected.paths[i], actual.paths[i])
        //        }
    } else {
        assertEquals(expected, actual)
    }
}

fun randomVector(): Point {
    return Point(Random.nextFloat(), Random.nextFloat())
}

fun assertEquals(expected: CircleIntersectionUtil.Result, actual: CircleIntersectionUtil.Result) {
    println(expected::class)
    println(actual::class)
    assertEquals(expected::class, actual::class, message = run {
        "Incorrect class types. expected: $expected, actual: $actual"
    })
    when (expected) {
        CircleIntersectionUtil.Result.NoIntersection -> Unit
        CircleIntersectionUtil.Result.CircleFullyCovers -> Unit
        is CircleIntersectionUtil.Result.PartialIntersection -> {
            val a = actual as CircleIntersectionUtil.Result.PartialIntersection
            assertEquals(expected.firstPointInside, a.firstPointInside)
            assertEquals(expected.secondPointInside, a.secondPointInside)
            assertPointEquals(expected.intersectionPoint, a.intersectionPoint)
        }
        is CircleIntersectionUtil.Result.FullIntersection -> {
            val a = actual as CircleIntersectionUtil.Result.FullIntersection
            assertPointEquals(expected.firstIntersectionPoint, a.firstIntersectionPoint)
            assertPointEquals(expected.secondIntersectionPoint, a.secondIntersectionPoint)
        }
        is CircleIntersectionUtil.Result.Touching -> {
            assertEquals(expected, actual)
        }
        is CircleIntersectionUtil.Result.Tangent -> {
            val a = actual as CircleIntersectionUtil.Result.Tangent
            assertPointEquals(expected.tangentPoint, a.tangentPoint)
        }
    }
}

fun doublesSimilar(pathLength1: Double, pathLength2: Double, maxDiff: Double): Boolean {
    return abs(pathLength1 - pathLength2) <= maxDiff
}

