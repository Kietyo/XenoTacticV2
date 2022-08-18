import com.soywiz.korio.async.runBlockingNoJs
import com.xenotactic.gamelogic.korge_utils.TEST_TEMP_DATA_VFS
import com.xenotactic.gamelogic.korge_utils.loadGameMapFromGoldenAsync
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity

import korge.model.korge_test_utils.createTempFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class KorgeGameMapTest {
    @Test
    fun serialization_assureThatBlockingPointsGetReinitializedForRock() = runBlockingNoJs {
        val jsonFormatter = Json {
            prettyPrint = true
        }

        val gameMap = GameMap.create(
            4, 4,
            MapEntity.Rock(1, 1, 2, 2)
        )

        val blockingPoints = gameMap.blockingPointsView()

        assertFalse(blockingPoints.contains(0, 0))
        assertFalse(blockingPoints.contains(0, 1))
        assertFalse(blockingPoints.contains(0, 2))
        assertFalse(blockingPoints.contains(0, 3))

        assertFalse(blockingPoints.contains(1, 0))
        assertTrue(blockingPoints.contains(1, 1))
        assertTrue(blockingPoints.contains(1, 2))
        assertFalse(blockingPoints.contains(1, 3))

        assertFalse(blockingPoints.contains(2, 0))
        assertTrue(blockingPoints.contains(2, 1))
        assertTrue(blockingPoints.contains(2, 2))
        assertFalse(blockingPoints.contains(2, 3))

        assertFalse(blockingPoints.contains(3, 0))
        assertFalse(blockingPoints.contains(3, 1))
        assertFalse(blockingPoints.contains(3, 2))
        assertFalse(blockingPoints.contains(3, 3))

        val encodedJson = jsonFormatter.encodeToString(gameMap)

        val file = TEST_TEMP_DATA_VFS().createTempFile()

        file.writeString(encodedJson)

        val readJsonString = file.readString()
        println(
            """
            readJsonString:
            $readJsonString
        """.trimIndent()
        )

        val readGameMap = jsonFormatter.decodeFromString<GameMap>(readJsonString)
        val readBlockingPoints = readGameMap.blockingPointsView()
        assertFalse(readBlockingPoints.contains(0, 0))
        assertFalse(readBlockingPoints.contains(0, 1))
        assertFalse(readBlockingPoints.contains(0, 2))
        assertFalse(readBlockingPoints.contains(0, 3))

        assertFalse(readBlockingPoints.contains(1, 0))
        assertTrue(readBlockingPoints.contains(1, 1))
        assertTrue(readBlockingPoints.contains(1, 2))
        assertFalse(readBlockingPoints.contains(1, 3))

        assertFalse(readBlockingPoints.contains(2, 0))
        assertTrue(readBlockingPoints.contains(2, 1))
        assertTrue(readBlockingPoints.contains(2, 2))
        assertFalse(readBlockingPoints.contains(2, 3))

        assertFalse(readBlockingPoints.contains(3, 0))
        assertFalse(readBlockingPoints.contains(3, 1))
        assertFalse(readBlockingPoints.contains(3, 2))
        assertFalse(readBlockingPoints.contains(3, 3))

        file.delete()

        Unit
    }

    @Test
    fun serialization_assureThatBlockingPointsGetReinitializedForTower() = runBlockingNoJs {
        val jsonFormatter = Json {
            prettyPrint = true
        }

        val gameMap = GameMap.create(
            4, 4,
            MapEntity.Tower(1, 1)
        )

        val blockingPoints = gameMap.blockingPointsView()

        assertFalse(blockingPoints.contains(0, 0))
        assertFalse(blockingPoints.contains(0, 1))
        assertFalse(blockingPoints.contains(0, 2))
        assertFalse(blockingPoints.contains(0, 3))

        assertFalse(blockingPoints.contains(1, 0))
        assertTrue(blockingPoints.contains(1, 1))
        assertTrue(blockingPoints.contains(1, 2))
        assertFalse(blockingPoints.contains(1, 3))

        assertFalse(blockingPoints.contains(2, 0))
        assertTrue(blockingPoints.contains(2, 1))
        assertTrue(blockingPoints.contains(2, 2))
        assertFalse(blockingPoints.contains(2, 3))

        assertFalse(blockingPoints.contains(3, 0))
        assertFalse(blockingPoints.contains(3, 1))
        assertFalse(blockingPoints.contains(3, 2))
        assertFalse(blockingPoints.contains(3, 3))

        val encodedJson = jsonFormatter.encodeToString(gameMap)

        val file = TEST_TEMP_DATA_VFS().createTempFile()

        file.writeString(encodedJson)

        val readJsonString = file.readString()
        println(
            """
            readJsonString:
            $readJsonString
        """.trimIndent()
        )

        val readGameMap = jsonFormatter.decodeFromString<GameMap>(readJsonString)
        val readBlockingPoints = readGameMap.blockingPointsView()
        assertFalse(readBlockingPoints.contains(0, 0))
        assertFalse(readBlockingPoints.contains(0, 1))
        assertFalse(readBlockingPoints.contains(0, 2))
        assertFalse(readBlockingPoints.contains(0, 3))

        assertFalse(readBlockingPoints.contains(1, 0))
        assertTrue(readBlockingPoints.contains(1, 1))
        assertTrue(readBlockingPoints.contains(1, 2))
        assertFalse(readBlockingPoints.contains(1, 3))

        assertFalse(readBlockingPoints.contains(2, 0))
        assertTrue(readBlockingPoints.contains(2, 1))
        assertTrue(readBlockingPoints.contains(2, 2))
        assertFalse(readBlockingPoints.contains(2, 3))

        assertFalse(readBlockingPoints.contains(3, 0))
        assertFalse(readBlockingPoints.contains(3, 1))
        assertFalse(readBlockingPoints.contains(3, 2))
        assertFalse(readBlockingPoints.contains(3, 3))

        file.delete()

        Unit
    }

    @Test
    fun testDecodeMap() = runBlockingNoJs {
        val map = loadGameMapFromGoldenAsync("00001.json")
        println(map)
        println(map.hashCode())
    }
}