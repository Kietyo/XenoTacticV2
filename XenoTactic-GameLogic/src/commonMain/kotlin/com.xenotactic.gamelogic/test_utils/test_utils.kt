package com.xenotactic.gamelogic.test_utils

import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.korge_utils.readStringOrNull
import com.xenotactic.gamelogic.korge_utils.toGameMap
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.CircleIntersectionUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

val GOLDEN_IDS_FILE = com.xenotactic.gamelogic.korge_utils.TEST_DATA_VFS["golden_ids.json"]


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

const val TEST_FLOAT_MAX_DELTA = 0.00001f
const val TEST_DOUBLE_MAX_DELTA = 0.00001

fun pointEquals(actual: Point, expected: Point): Boolean {
    return doubleEquals(actual.x, expected.x) && doubleEquals(actual.y, expected.y)
}

fun doubleEquals(d1: Double, d2: Double, threshold: Double = TEST_DOUBLE_MAX_DELTA): Boolean {
    return (d1 - d2).absoluteValue <= threshold
}

fun generateRandomFileName(length: Int = 6): String {
    val random = Random
    val sb = StringBuilder()
    repeat(length) {
        sb.append(random.nextInt(0, 10))
    }
    return sb.toString()
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