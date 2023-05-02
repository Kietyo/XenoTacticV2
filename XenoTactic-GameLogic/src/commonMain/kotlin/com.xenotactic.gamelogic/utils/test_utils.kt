package com.xenotactic.gamelogic.utils

import korlibs.io.async.runBlockingNoSuspensions
import korlibs.io.file.VfsFile

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.assertTrue

val GOLDEN_IDS_FILE = TEST_DATA_VFS["golden_ids.json"]

const val TEST_FLOAT_MAX_DELTA = 0.00001f
const val TEST_DOUBLE_MAX_DELTA = 0.00001

@Serializable
data class MapIds private constructor(
    val ids: List<String>
) {
    companion object {
        fun create(ids: Collection<String>): MapIds {
            val setSize = ids.toSet().size
            require(setSize == ids.size) {
                "Expected no duplicate ids. Expected: $setSize. Actual: ${ids.size}"
            }
            return MapIds(ids.sorted())
        }
    }
}

fun getGoldenMapIds(): MapIds {
    val data = GOLDEN_IDS_FILE.readStringOrNull() ?: return MapIds.create(emptyList())
    return Json.decodeFromString<MapIds>(data)
}


fun writeGoldenMapIds(mapIds: MapIds) {
    runBlockingNoSuspensions {
        val json = Json {
            prettyPrint = true
        }
        GOLDEN_IDS_FILE.writeString(json.encodeToString(mapIds))
    }
}

suspend fun VfsFile.createTempFile(extension: String = "txt"): VfsFile {
    while (true) {
        val tempFileName = "${generateRandomFileName()}.$extension"
        val possibleTempFile = this.get(tempFileName)
        if (!possibleTempFile.exists()) {
            println("Creating temp file: $possibleTempFile")
            return possibleTempFile
        }
    }
}



fun assertPointSetEquals(expect: Set<IPoint>, actual: Set<IPoint>) {
    kotlin.test.assertEquals(expect.size, actual.size)
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
    kotlin.test.assertEquals(expected, actual, TEST_FLOAT_MAX_DELTA)
}

fun assertFloatEquals(expected: Float, actual: Double) {
    kotlin.test.assertEquals(expected.toDouble(), actual, TEST_DOUBLE_MAX_DELTA)
}

fun assertFloatEquals(expected: Float, actual: GameUnit) {
    assertFloatEquals(expected, actual.toDouble())
}

fun assertDoubleEquals(expected: Double, actual: Double) {
    kotlin.test.assertEquals(expected, actual, TEST_DOUBLE_MAX_DELTA)
}

fun assertDoubleEquals(expected: GameUnit, actual: GameUnit) {
    assertDoubleEquals(expected.toDouble(), actual.toDouble())
}

fun assertDoubleEquals(expected: Double, actual: GameUnit) {
    assertDoubleEquals(expected, actual.toDouble())
}


fun assertPointEquals(expected: GameUnitTuple, actual: GameUnitTuple) {
    assertPointEquals(expected.toPoint(), actual.toPoint())
}

fun assertPointEquals(expected: IPoint, actual: GameUnitTuple) {
    assertPointEquals(expected, actual.toPoint())
}

fun assertPointEquals(expected: IPoint, actual: IPoint) {
    assertTrue(
        pointEquals(expected, actual), message =
        """Points are not equal.
        expected: $expected        ,
        actual: $actual
    """.trimIndent()
    )
}

fun pointEquals(actual: IPoint, expected: IPoint): Boolean {
    return doubleEquals(actual.x, expected.x) && doubleEquals(actual.y, expected.y)
}

fun doubleEquals(d1: Double, d2: Double, threshold: Double = TEST_DOUBLE_MAX_DELTA): Boolean {
    return (d1 - d2).absoluteValue <= threshold
}

fun assertPathEquals(expected: Path, actual: Path) {
    kotlin.test.assertEquals(expected.numPoints, actual.numPoints)
    for (i in 0 until expected.numPoints) {
        assertPointEquals(expected.points[i], actual.points[i])
    }
}

fun assertPathSequenceEquals(expected: PathSequence?, actual: PathSequence?) {
    if (expected == null && actual == null) return

    if (expected != null && actual != null) {
        kotlin.test.assertEquals(expected.numPaths, actual.numPaths)
        kotlin.test.assertEquals(expected.paths, actual.paths)
        //        for (i in 0 until expected.numPaths) {
        //            assertPathEquals(expected.paths[i], actual.paths[i])
        //        }
    } else {
        kotlin.test.assertEquals(expected, actual)
    }
}

fun randomVector(): IPoint {
    return IPoint(Random.nextFloat(), Random.nextFloat())
}

fun generateRandomFileName(length: Int = 6): String {
    val random = Random
    val sb = StringBuilder()
    repeat(length) {
        sb.append(random.nextInt(0, 10))
    }
    return sb.toString()
}

fun assertEquals(expected: CircleIntersectionUtil.Result, actual: CircleIntersectionUtil.Result) {
    println(expected::class)
    println(actual::class)
    kotlin.test.assertEquals(expected::class, actual::class, message = run {
        "Incorrect class types. expected: $expected, actual: $actual"
    })
    when (expected) {
        CircleIntersectionUtil.Result.NoIntersection -> Unit
        CircleIntersectionUtil.Result.CircleFullyCovers -> Unit
        is CircleIntersectionUtil.Result.PartialIntersection -> {
            val a = actual as CircleIntersectionUtil.Result.PartialIntersection
            kotlin.test.assertEquals(expected.firstPointInside, a.firstPointInside)
            kotlin.test.assertEquals(expected.secondPointInside, a.secondPointInside)
            assertPointEquals(expected.intersectionPoint, a.intersectionPoint)
        }
        is CircleIntersectionUtil.Result.FullIntersection -> {
            val a = actual as CircleIntersectionUtil.Result.FullIntersection
            assertPointEquals(expected.firstIntersectionPoint, a.firstIntersectionPoint)
            assertPointEquals(expected.secondIntersectionPoint, a.secondIntersectionPoint)
        }
        is CircleIntersectionUtil.Result.Touching -> {
            kotlin.test.assertEquals(expected, actual)
        }
        is CircleIntersectionUtil.Result.Tangent -> {
            val a = actual as CircleIntersectionUtil.Result.Tangent
            assertPointEquals(expected.tangentPoint, a.tangentPoint)
        }
    }
}

fun doublesSimilar(pathLength1: GameUnit, pathLength2: GameUnit, maxDiff: Double): Boolean {
    return doublesSimilar(pathLength1.toDouble(), pathLength2.toDouble(), maxDiff)
}

fun doublesSimilar(pathLength1: Double, pathLength2: Double, maxDiff: Double): Boolean {
    return abs(pathLength1 - pathLength2) <= maxDiff
}

