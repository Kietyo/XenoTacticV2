import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localCurrentDirVfs
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

val TEST_TEMP_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata/TEMP"].apply {
        mkdir()
    }
}

val TEST_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata"].apply {
        mkdir()
    }
}

fun VfsFile.existsBlocking(): Boolean {
    return runBlockingNoSuspensions { this.exists() }
}

inline fun <reified T> VfsFile.decodeJson(): T? {
    if (existsBlocking()) {
        return Json.decodeFromString<T>(this.readStringBlocking())
    }
    return null
}

fun VfsFile.listSimpleBlocking(): List<VfsFile> {
    return runBlockingNoSuspensions { listSimple() }
}

fun VfsFile.readStringOrNull(): String? {
    if (existsBlocking()) {
        return readStringBlocking()
    }
    return null
}

fun VfsFile.readStringBlocking(): String {
    return runBlockingNoSuspensions { readString() }
}

fun VfsFile.toGameMap(): GameMap? {
    return this.decodeJson<GameMap>()
}

val GOLDENS_DATA_VFS = runBlockingNoSuspensions {
    localCurrentDirVfs["src/commonTest/testdata/goldens"].apply {
        mkdir()
    }
}

val GOLDEN_IDS_FILE = TEST_DATA_VFS["golden_ids.json"]

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