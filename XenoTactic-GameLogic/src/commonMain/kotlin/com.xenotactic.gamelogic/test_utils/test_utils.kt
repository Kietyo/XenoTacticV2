package com.xenotactic.gamelogic.test_utils

import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.CircleIntersectionUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import toGameMap
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

val TEST_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata"].apply {
        mkdir()
    }
}

val GOLDENS_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata/goldens"].apply {
        mkdir()
    }
}

fun getGoldenJsonFiles(): List<VfsFile> {
    return runBlockingNoSuspensions { GOLDENS_DATA_VFS.listSimple() }
}

fun getAllGoldenMaps(): List<GameMap> {
    return getGoldenJsonFiles().map { it.toGameMap()!! }
}

/**
 * Loads game maps found in testdata/goldens.
 *
 * E.g: Given a fileName of "00001.json" loads the game map at
 * testdata/goldens/00001.json
 */
suspend fun loadGameMapFromGoldensAsync(fileName: String): GameMap {
    return Json.decodeFromString<GameMap>(GOLDENS_DATA_VFS["$fileName"].readString())
}

/**
 * Loads game maps found in testdata/goldens.
 *
 * E.g: Given a fileName of "00001.json" loads the game map at
 * testdata/goldens/00001.json
 */
fun loadGameMapFromGoldensBlocking(fileName: String): GameMap {
    return runBlockingNoSuspensions { Json.decodeFromString<GameMap>(GOLDENS_DATA_VFS["$fileName"].readString()) }
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