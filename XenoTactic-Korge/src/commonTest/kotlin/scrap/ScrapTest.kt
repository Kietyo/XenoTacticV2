package scrap

import GOLDENS_DATA_VFS
import TEST_DATA_VFS
import com.soywiz.kds.iterators.parallelMap
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.VfsFile
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import listSimpleBlocking
import pathing.PathFinder
import random.MapGeneratorConfiguration
import random.RandomMapGenerator
import toGameMap
import kotlin.test.Test
import kotlin.test.assertTrue

internal class ScrapTest {

    @Test
    fun scrap1() {
        val entity = MapEntity.TeleportIn(1, IntPoint(2, 3))

        println(Json.encodeToString(entity))
    }

    @Test
    fun testJson() {
        val formatter = Json {
            prettyPrint = true
        }

        val map = RandomMapGenerator.generate(
            MapGeneratorConfiguration(
                20,
                30,
                3,
                30,
                2
            )
        )

        val jsonData = formatter.encodeToString(map.map)
        println(jsonData)

        runBlockingNoJs {
            val testJson = TEST_DATA_VFS["test.json"]
            testJson.writeString(jsonData)
        }
    }

    @Test
    fun verifyGoldens() {
        val jsonFiles = GOLDENS_DATA_VFS.listSimpleBlocking()

        val mutex = Mutex()

        val failures = mutableListOf<String>()

        val runTest = ret@{ json: VfsFile ->
            println("Testing: $json")
            val gameMap = json.toGameMap()!!
            if (PathFinder.getShortestPath(gameMap) == null) {
                runBlockingNoSuspensions { mutex.lock() }
                failures.add(json.toString())
                mutex.unlock()
            }
        }

        jsonFiles.parallelMap(runTest)

        assertTrue(failures.isEmpty(), message = run {
            "Failures:\n${failures.joinToString(separator = "\n")}"
        })
    }
}