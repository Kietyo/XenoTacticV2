package scrap

import korlibs.io.async.suspendTest
import korlibs.io.file.VfsFile
import com.xenotactic.gamelogic.utils.GOLDENS_DATA_VFS
import com.xenotactic.gamelogic.utils.TEST_DATA_VFS
import com.xenotactic.gamelogic.utils.toGameMap
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import pathing.PathFinder
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlin.test.Test

internal class ScrapTest {

    @Test
    fun scrap1() {
        val entity = MapEntity.TeleportIn(1, GameUnitTuple(2, 3))

        println(Json.encodeToString(entity))
    }

    @Test
    fun testJson() = suspendTest {
        val formatter = Json {
            prettyPrint = true
        }

        val map = RandomMapGenerator.generate(
            MapGeneratorConfiguration(
                20,
                30.toGameUnit(),
                3.toGameUnit(),
                30,
                2
            )
        )

        val jsonData = formatter.encodeToString(map.map)
        println(jsonData)

            val testJson = TEST_DATA_VFS["test.json"]
            testJson.writeString(jsonData)
    }

    @Test
    fun verifyGoldens() = suspendTest {
        val jsonFiles = GOLDENS_DATA_VFS().listSimple()

        val mutex = Mutex()

        val failures = mutableListOf<String>()

        val runTest = ret@{ json: VfsFile ->
            println("Testing: $json")
            val gameMap = json.toGameMap()!!
            if (PathFinder.getShortestPath(gameMap) == null) {
                suspendTest {
                    mutex.lock()
                }
                failures.add(json.toString())
                mutex.unlock()
            }
        }

//        jsonFiles.parallelMap(runTest)
//
//        assertTrue(failures.isEmpty(), message = run {
//            "Failures:\n${failures.joinToString(separator = "\n")}"
//        })
    }
}