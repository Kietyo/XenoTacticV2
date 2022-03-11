import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.GameMap
import korge_utils.toGameMap

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