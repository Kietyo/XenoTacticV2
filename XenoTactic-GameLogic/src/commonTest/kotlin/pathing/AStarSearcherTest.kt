package pathing

import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.baseName
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.korge_utils.GOLDENS_DATA_VFS
import com.xenotactic.gamelogic.korge_utils.loadGameMapFromGoldenAsync
import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.EntityPath
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence


import com.xenotactic.gamelogic.utils.measureTime
import test_utils.doublesSimilar
import utils.StatCounterMap

import kotlin.test.*

internal class AStarSearcherTest {

    //    @Test
    //    fun findInvalidRandomlyGeneratedMap() {
    //        val map: GameMap
    //        while (true) {
    //            val genMap = RandomMapGenerator.generate(
    //                MapGeneratorConfiguration(
    //                    GAME_WIDTH, GAME_HEIGHT,
    //                    4,
    //                    10,
    //                    0,
    //                    AStarSearcher,
    //                    500,
    //                    returnMapAsIsOnFailure = true
    //                )
    //            )
    //            if (!genMap.first) {
    //                map = genMap.second!!
    //                break
    //            }
    //        }
    //
    //        println(map)
    //
    //        val engine = Engine()
    //        engine.setOneTimeComponent(GameMapComponent(engine))
    //        val game = Game(engine)
    //        engine.getOneTimeComponent<GameMapComponent>().updateMap(map)
    //
    //        Lwjgl3Application(game, Lwjgl3ApplicationConfiguration().apply {
    //            setTitle("TestGame")
    //            setWindowedMode(1280, 720)
    //        })
    //    }
    //
    //    @Test
    //    fun profiling_base() {
    //        var aCounter = IntStatCounter("aCounter")
    //        var timeCounter = IntStatCounter("timeCounter")
    //        repeat(1000) {
    //            val map: GameMap
    //            while (true) {
    //                val genMap = RandomMapGenerator.generate(
    //                    MapGeneratorConfiguration(
    //                        30, 20,
    //                        6,
    //                        20,
    //                        0,
    //                        AStarSearcher,
    //                        1000,
    //                        returnMapAsIsOnFailure = false
    //                    )
    //                ).second
    //                if (genMap != null) {
    //                    map = genMap
    //                    break
    //                }
    //            }
    //            COUNTERS.clear()
    //            A_STAR_SEARCHER_COUNTER.clear()
    //
    //            var resultA = measureTime {
    //                map.searcher = AStarSearcher
    //                map.getShortestPath()
    //            }
    //
    //            aCounter.add(A_STAR_SEARCHER_COUNTER)
    //            timeCounter.record((resultA.first / 1000.0).toInt())
    //        }
    //
    //        println(timeCounter)
    //        println(aCounter)
    //    }
    //
    //    @Test
    //    fun profiling_baseAndTest() {
    //        var aCounter = IntStatCounter("aCounter")
    //        var bCounter = IntStatCounter("bCounter")
    //        val random = Random.Default
    //        var timeA = 0L
    //        var timeB = 0L
    //        repeat(500) {
    //            val map = RandomMapGenerator.generate(
    //                MapGeneratorConfiguration(
    //                    30, 20,
    //                    4,
    //                    10,
    //                    0,
    //                    AStarSearcher
    //                )
    //            ).second!!
    //            COUNTERS.clear()
    //            A_STAR_SEARCHER_COUNTER.clear()
    //            A_STAR_SEARCHER_2_COUNTER.clear()
    //
    //            var resultA: Pair<Long, PathSequence?>
    //            var resultB: Pair<Long, PathSequence?>
    //            if (random.nextBoolean()) {
    //                resultA = measureTime {
    //                    map.searcher = AStarSearcher
    //                    map.getShortestPath()
    //                }
    //
    //                resultB = measureTime {
    //                    map.searcher = AStarSearcher2
    //                    map.getShortestPath()
    //                }
    //            } else {
    //                resultB = measureTime {
    //                    map.searcher = AStarSearcher2
    //                    map.getShortestPath()
    //                }
    //                resultA = measureTime {
    //                    map.searcher = AStarSearcher
    //                    map.getShortestPath()
    //                }
    //            }
    //
    //            aCounter.add(A_STAR_SEARCHER_COUNTER)
    //            bCounter.add(A_STAR_SEARCHER_2_COUNTER)
    //
    //            val resultAPathLength = resultA.second!!.pathLength
    //            val resultBPathLength = resultB.second!!.pathLength
    //
    //            timeA += resultA.first
    //            timeB += resultB.first
    //
    //            if (abs(resultAPathLength - resultBPathLength) > 10) {
    //                println(
    //                    "resultAPathLength: $resultAPathLength, resultBPathLength: " +
    //                            "$resultBPathLength, diff: ${abs(resultAPathLength - resultBPathLength)}"
    //                )
    //                assertEquals(
    //                    resultA.second, resultB.second,
    //                    """
    //                    timeA: $timeA, timeB: $timeB,
    //                    $aCounter,
    //                    $bCounter
    //                """.trimIndent()
    //                )
    //            }
    //        }
    //
    //        println("timeA: $timeA, timeB: $timeB")
    //        println(aCounter)
    //        println(bCounter)
    //    }

    @Test
    fun getShortestPath() {
        val searcher = AStarSearcher
        println(
            searcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Start(3, 8),
                    MapEntity.Finish(3, 0)
                ),
                teleportPairs = listOf(
                    TeleportPair(
                        MapEntity.TeleportIn(0, IntPoint(3, 3)),
                        MapEntity.TeleportOut(0, IntPoint(7, 7))
                    )
                ), blockingEntities = listOf(
                    MapEntity.ROCK_1X1.at(3, 4),
                    MapEntity.ROCK_1X1.at(4, 4)
                )
            )
        )
    }

    @Test
    fun getShortestPath2() {
        assertEquals(
            PathSequence.create(
                Path.create(4 to 9, 4 to 5),
                Path.create(8 to 8, 4 to 1)
            ),
            AStarSearcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Start(3, 8),
                    MapEntity.Finish(3, 0)
                ),
                teleportPairs = listOf(
                    TeleportPair(
                        MapEntity.TeleportIn(0, IntPoint(3, 3)),
                        MapEntity.TeleportOut(0, IntPoint(7, 7))
                    )
                )
            )?.toPathSequence()
        )
    }

    @Test
    fun getUpdatablePath2() {
        assertEquals(
            GamePath(
                listOf(
                    EntityPath.EntityToEntityIntersectsTeleport(
                        MapEntity.Start(3, 8),
                        MapEntity.Finish(3, 0),
                        0,
                        Path.create(4 to 9, 4 to 1),
                        Path.create(4 to 9, 4 to 5)
                    ),
                    EntityPath.EntityToEntityPath(
                        MapEntity.TeleportOut(0, 7, 7),
                        MapEntity.Finish(3, 0),
                        Path.create(8 to 8, 4 to 1)
                    )
                ),
            ),
            AStarSearcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Start(3, 8),
                    MapEntity.Finish(3, 0)
                ),
                teleportPairs = listOf(
                    TeleportPair(
                        MapEntity.TeleportIn(0, IntPoint(3, 3)),
                        MapEntity.TeleportOut(0, IntPoint(7, 7))
                    )
                )
            )
        )
    }

    @Test
    fun getUpdatablePath3() {
        assertEquals(
            GamePath(
                listOf(
                    EntityPath.EntityToEntityIntersectsTeleport(
                        MapEntity.Start(0, 8),
                        MapEntity.Checkpoint(0, 8, 8),
                        0,
                        Path.create(1 to 9, 9 to 9),
                        Path.create(1 to 9, 4 to 9)
                    ),
                    EntityPath.EntityToEntityPath(
                        MapEntity.TeleportOut(0, 6, 8),
                        MapEntity.Checkpoint(0, 8, 8),
                        Path.create(7 to 9, 9 to 9)
                    ),
                    EntityPath.EntityToEntityIntersectsTeleport(
                        MapEntity.Checkpoint(0, 8, 8),
                        MapEntity.Finish(8, 0),
                        1,
                        Path.create(9 to 9, 9 to 1),
                        Path.create(9 to 9, 9 to 7)
                    ),
                    EntityPath.EntityToEntityPath(
                        MapEntity.TeleportOut(1, 8, 3),
                        MapEntity.Finish(8, 0),
                        Path.create(9 to 4, 9 to 1)
                    )
                )
            ),
            AStarSearcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Start(0, 8),
                    MapEntity.Checkpoint(0, 8, 8),
                    MapEntity.Finish(8, 0)
                ),
                teleportPairs = listOf(
                    MapEntity.TeleportIn(0, 4, 8) to MapEntity.TeleportOut(0, 6, 8),
                    MapEntity.TeleportIn(1, 8, 5) to MapEntity.TeleportOut(1, 8, 3),
                )
            )
        )
    }

    @Test
    fun getUpdatablePath4() {
        assertEquals(
            GamePath(
                listOf(
                    EntityPath.EntityToEntityIntersectsTeleport(
                        MapEntity.Start(0, 8),
                        MapEntity.Checkpoint(0, 8, 8),
                        0,
                        Path.create(1 to 9, 9 to 9),
                        Path.create(1 to 9, 4 to 9)
                    ),
                    EntityPath.EntityToEntityPath(
                        MapEntity.TeleportOut(0, 6, 8),
                        MapEntity.Checkpoint(0, 8, 8),
                        Path.create(7 to 9, 9 to 9)
                    ),
                    EntityPath.EntityToEntityIntersectsTeleport(
                        MapEntity.Checkpoint(0, 8, 8),
                        MapEntity.Checkpoint(1, 8, 0),
                        1,
                        Path.create(9 to 9, 9 to 1),
                        Path.create(9 to 9, 9 to 7)
                    ),
                    EntityPath.EntityToEntityPath(
                        MapEntity.TeleportOut(1, 8, 3),
                        MapEntity.Checkpoint(1, 8, 0),
                        Path.create(9 to 4, 9 to 1)
                    ),
                    EntityPath.EntityToEntityIntersectsTeleport(
                        MapEntity.Checkpoint(1, 8, 0),
                        MapEntity.Finish(0, 0),
                        2,
                        Path.create(9 to 1, 1 to 1),
                        Path.create(9 to 1, 7 to 1)
                    ),
                    EntityPath.EntityToEntityPath(
                        MapEntity.TeleportOut(2, 3, 0),
                        MapEntity.Finish(0, 0),
                        Path.create(4 to 1, 1 to 1)
                    )
                )
            ),
            AStarSearcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Start(0, 8),
                    MapEntity.Checkpoint(0, 8, 8),
                    MapEntity.Checkpoint(1, 8, 0),
                    MapEntity.Finish(0, 0)
                ),
                teleportPairs = listOf(
                    MapEntity.TeleportIn(0, 4, 8) to MapEntity.TeleportOut(0, 6, 8),
                    MapEntity.TeleportIn(1, 8, 5) to MapEntity.TeleportOut(1, 8, 3),
                    MapEntity.TeleportIn(2, 5, 0) to MapEntity.TeleportOut(2, 3, 0),
                )
            )
        )
    }

    @Test
    fun regressionTest1() {
        assertEquals(
            PathSequence.create(
                Path.create(
                    Point(4.0, 7.05),
                    Point(6.007071067811865, 7.007071067811865),
                    Point(8, 1)
                )
            ),
            AStarSearcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Checkpoint(0, 3, 6),
                    MapEntity.Finish(7, 0)
                ),
                blockingEntities = listOf(MapEntity.Rock(2, 5, 4, 2))
            )?.toPathSequence()
        )
    }

    @Test
    fun regressionTest2() {
        assertEquals(
            PathSequence.create(
                Path.create(
                    Point(2.0, 2.05),
                    Point(2.9646446609406727, 2.0353553390593273),
                )
            ),
            AStarSearcher.getUpdatablePath(
                10, 10,
                listOf(
                    MapEntity.Checkpoint(0, 1, 1),
                    MapEntity.Finish(2, 1)
                ),
                blockingEntities = listOf(
                    MapEntity.Rock(1, 0, 4, 2),
                    MapEntity.Rock(3, 0, 2, 4),
                )
            )?.toPathSequence()
        )
    }

    @Test
    fun regressionTest() = runBlockingNoJs ret@{
        val searcher1 = AStarSearcher
        val searcher2 = AStarSearcher

        if (searcher1 == searcher2) {
            return@ret
        }

        val jsonFiles = GOLDENS_DATA_VFS().listSimple()

        val expectedRegressions = setOf<String>(
            "sadfsaf",
            //            "01600.json",
            //            "01748.json",
            //            "00646.json"
        )

        val counters = StatCounterMap("Regression test counter")

        var numVerified = 0
        for ((i, json) in jsonFiles.withIndex()) {
            if (expectedRegressions.contains(json.baseName)) continue
            println("${json.baseName}: verifying $json")
            numVerified++
            val jsonString = json.readString()

            val gameMap = Json.decodeFromString<GameMap>(jsonString)

            val results1 = measureTime { PathFinder.getShortestPath(gameMap, searcher1) }
            val path1 = results1.second
            counters.getIntCounter("base time").record(results1.first)

            val results2 = measureTime { PathFinder.getShortestPath(gameMap, searcher2) }
            val path2 = results2.second
            counters.getIntCounter("test time").record(results2.first)

            if (path1 == null && path2 == null) {
                continue
            }

            if (path1 != null && path2 == null) {
                fail(
                    """
                            path1 is $path1
                            path2 is $path2
                        """.trimIndent()
                )
            }

            if (!doublesSimilar(path1!!.pathLength, path2!!.pathLength, 0.5)) {
                fail(
                    """
                        Path lengths are not similar!
                        path1 length: ${path1.pathLength}
                        path2 length: ${path2.pathLength}
                    """.trimIndent()
                )
            }

            //            assertPathSequenceEquals(
            //                path1,
            //                path2
            //            )

            println(
                """
                numVerified: $numVerified
            """.trimIndent()
            )
        }

        println(counters)
        Unit
    }

    @Test
    fun regressionTest00001() = runBlockingNoJs {
        val map = loadGameMapFromGoldenAsync("00001.json")

        //        val map =
        //            Json.decodeFromString<GameMap>(localCurrentDirVfs["src/commonTest/testdata/goldens"].apply {
        //                mkdir()
        //            }["00001.json"].readString())

        val path = PathFinder.getShortestPath(map, AStarSearcher)
        assertNotNull(path)

        Unit
    }
}