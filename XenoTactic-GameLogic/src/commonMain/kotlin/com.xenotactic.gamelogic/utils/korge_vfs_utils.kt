package com.xenotactic.gamelogic.utils

import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.localCurrentDirVfs
import com.xenotactic.gamelogic.model.GameMap
import korlibs.io.file.VfsFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

suspend fun TEST_TEMP_DATA_VFS() =
    localCurrentDirVfs["src/commonTest/testdata/TEMP"].apply {
        mkdir()
    }

val TEST_DATA_VFS = runBlockingNoJs {
    localCurrentDirVfs["src/commonTest/testdata"].apply {
        mkdir()
    }
}

suspend fun GOLDENS_DATA_VFS() =
    localCurrentDirVfs["src/commonTest/testdata/goldens"].apply {
        mkdir()
    }

fun VfsFile.existsBlocking(): Boolean {
    return runBlockingNoJs { exists() }
}

fun VfsFile.readStringOrNull(): String? {
    if (existsBlocking()) {
        return readStringBlocking()
    }
    return null
}

fun VfsFile.readStringBlocking(): String {
    return runBlockingNoJs { readString() }
}

fun VfsFile.toGameMap(): GameMap? {
    return decodeJsonBlocking<GameMap>()
}

inline fun <reified T> VfsFile.decodeJsonBlocking(): T? {
    if (existsBlocking()) {
        return Json.decodeFromString<T>(this.readStringBlocking())
    }
    return null
}

inline suspend fun <reified T> VfsFile.decodeJson(): T? {
    if (exists()) {
        return Json.decodeFromString<T>(this.readString())
    }
    return null
}

fun getGoldenJsonFiles(): List<VfsFile> {
    return runBlockingNoJs { GOLDENS_DATA_VFS().listSimple() }
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
suspend fun loadGameMapFromGoldenAsync(fileName: String): GameMap {
    return Json.decodeFromString(GOLDENS_DATA_VFS()[fileName].readString())
}

/**
 * Loads game maps found in testdata/goldens.
 *
 * E.g: Given a fileName of "00001.json" loads the game map at
 * testdata/goldens/00001.json
 */
fun loadGameMapFromGoldenBlocking(fileName: String): GameMap {
    return runBlockingNoJs { Json.decodeFromString<GameMap>(GOLDENS_DATA_VFS()["$fileName"].readString()) }
}

