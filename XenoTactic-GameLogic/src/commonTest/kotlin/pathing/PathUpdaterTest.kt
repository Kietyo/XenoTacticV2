package pathing

import com.soywiz.kds.iterators.parallelMap
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.baseName
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.korge_utils.GOLDENS_DATA_VFS
import com.xenotactic.gamelogic.korge_utils.loadGameMapFromGoldenAsync
import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence


import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.gamelogic.utils.removeAllIndents
import solver.*
import test_utils.TowerPlacementLogger
import test_utils.doublesSimilar
import utils.StatCounterMap

import kotlin.random.Random
import kotlin.test.*

internal class PathUpdaterTest {

    @Test
    fun regressionTest1() {
        val gameMap = GameMap.create(
            10, 10,
            MapEntity.Start(3, 6),
            MapEntity.Finish(7, 0),
        )

        val pathUpdater = PathUpdater(gameMap)
        pathUpdater.placeEntity(MapEntity.Rock(2, 5, 4, 2))

        assertEquals(
            PathSequence.create(
                Path.create(
                    Point(4.0, 7.05),
                    Point(6.007071067811865, 7.007071067811865),
                    Point(8, 1)
                )
            ),
            pathUpdater.gamePath?.toPathSequence()
        )
    }

    @Test
    fun regressionTest2() {
        val gameMap = runBlockingNoJs { loadGameMapFromGoldenAsync("00253.json") }

        val pathUpdater = PathUpdater(gameMap)
        pathUpdater.placeEntities(
            MapEntity.Tower(0, 9),
            MapEntity.Tower(9, 31),
            MapEntity.Tower(0, 8),
            MapEntity.Tower(1, 15),
            MapEntity.Tower(6, 3),
            MapEntity.Tower(4, 25),
            MapEntity.Tower(5, 14),
            MapEntity.Tower(0, 22),
        )
        assertNull(pathUpdater.gamePath?.toPathSequence())
    }

    @Test
    @Ignore
    fun correctnessRegressionTest_solverPlacements() = runBlockingNoJs {
        val jsonFiles = GOLDENS_DATA_VFS().listSimple()

        val expectedRegressions = setOf<String>(
            "sadfsaf",
            //            "01600.json",
            //            "01748.json",
            //            "00646.json",
            //                        "01997.json",
        )

        val baseCounter = StatCounterMap("Base Counter")
        val testCounter = StatCounterMap("Test Counter")

        var numVerified = 0

        val runTest = ret@{ json: VfsFile ->
            if (expectedRegressions.contains(json.baseName)) return@ret
            println("${json.baseName}: verifying $json")
            numVerified++
            val jsonString = runBlockingNoJs { json.readString() }

            val gameMap1 = Json.decodeFromString<GameMap>(jsonString)
            val gameMap2 = Json.decodeFromString<GameMap>(jsonString)

            // This game map will be used to validate valid tower placements.
            // Only allow placements that DO NOT block the path.
            val gameMapForTowerPlacements = Json.decodeFromString<GameMap>(jsonString)

            val solverResult = StandardSolver3(
                SolverSettings(numSpotsToConsider = 1, numSpotsToExplore = 1)
            ).solve(
                gameMap1,
                SolverParams(15, OptimizationGoal.MaxPath)
            )

            when (solverResult) {
                SolverResult.Unsuccessful -> fail("$json: No solver results found?!")
                is SolverResult.Success -> {
                    val towerPlacements = solverResult.searchResult.state.towerPlacements
                    val pathUpdater = PathUpdater(gameMap2)
                    val log = StringBuilder()
                    val towerPlacementLogger = TowerPlacementLogger()
                    for ((i, placement) in towerPlacements.withIndex()) {
                        log.appendLine("\t$i, $placement")

                        val tower = MapEntity.Tower(placement)

                        gameMapForTowerPlacements.placeEntity(tower)
                        if (PathFinder.getShortestPath(gameMapForTowerPlacements, AStarSearcher)
                            == null
                        ) {
                            gameMapForTowerPlacements.removeEntity(tower)
                            log.appendLine("\t$i, IGNORED. Blocks path!")
                            continue
                        }

                        towerPlacementLogger.logTowerPlacement(tower)

                        val results1 = measureTime {
                            gameMap1.placeEntity(tower)
                            PathFinder.getShortestPath(gameMap1, AStarSearcher)
                        }
                        val path1 = results1.second
                        baseCounter.getTimeCounter("total").recordNanos(results1.first)
                        baseCounter.getTimeCounter("placement $i").recordNanos(results1.first)


                        val results2 = measureTime {
                            pathUpdater.placeEntity(tower)
                            pathUpdater.gamePath
                        }
                        val path2 = results2.second?.toPathSequence()
                        testCounter.getTimeCounter("total").recordNanos(results2.first)
                        testCounter.getTimeCounter("placement $i").recordNanos(results2.first)

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
                                Placements:
                                $log
                                
                                Placement code:
                                ${towerPlacementLogger.toTowerPlacementCodeString()}
                                
                                Path lengths are not similar!
                                path1 length: ${path1.pathLength}
                                path2 length: ${path2.pathLength}
                                """.removeAllIndents()
                            )
                        }
                    }
                }
            }

            println(
                """
                numVerified: $numVerified
            """.trimIndent()
            )

        }

        jsonFiles.parallelMap {
            runTest(it)
        }

        println(baseCounter)
        println(testCounter)
        Unit

    }

    @Test
    @Ignore
    fun correctnessRegressionTest_randomPlacements() = runBlockingNoJs {
        val numRandomPlacements = 15
        val jsonFiles = GOLDENS_DATA_VFS().listSimple()

        val expectedRegressions = setOf<String>(
            "01997.json",
        )

        val baseCounter = StatCounterMap("Base Counter")
        val testCounter = StatCounterMap("Test Counter")

        var numVerified = 0

        val runTest = ret@{ json: VfsFile ->
            if (expectedRegressions.contains(json.baseName)) {
                return@ret
            }

            println("${json.baseName}: verifying $json")
            numVerified++
            val jsonString = runBlockingNoJs { json.readString() }

            val gameMap1 = Json.decodeFromString<GameMap>(jsonString)
            val gameMap2 = Json.decodeFromString<GameMap>(jsonString)

            // This game map will be used to validate valid tower placements.
            // Only allow placements that DO NOT block the path.
            val gameMapForTowerPlacements = Json.decodeFromString<GameMap>(jsonString)

            val randomTowerPlacements =
                getAvailableTowerPlacementSpots(gameMap1).shuffled(Random(1337))
                    .take(numRandomPlacements)

            val pathUpdater = PathUpdater(gameMap2)
            val log = StringBuilder()
            val logTowerPlacementCode = StringBuilder()
            for ((i, placement) in randomTowerPlacements.withIndex()) {
                log.appendLine("\t$i, $placement")

                val tower = MapEntity.Tower(placement)

                gameMapForTowerPlacements.placeEntity(tower)
                if (PathFinder.getShortestPath(gameMapForTowerPlacements, AStarSearcher)
                    == null
                ) {
                    gameMapForTowerPlacements.removeEntity(tower)
                    log.appendLine("\t$i, IGNORED. Blocks path!")
                    continue
                }

                logTowerPlacementCode.appendLine(
                    "MapEntity.Tower(${placement.x}, ${placement.y}),"
                )

                val results1 = measureTime {
                    gameMap1.placeEntity(tower)
                    PathFinder.getShortestPath(gameMap1, AStarSearcher)
                }
                val path1 = results1.second
                baseCounter.getTimeCounter("total").recordNanos(results1.first)
                baseCounter.getTimeCounter("placement $i").recordNanos(results1.first)

                val results2 = measureTime {
                    pathUpdater.placeEntity(tower)
                    pathUpdater.gamePath
                }
                val path2 = results2.second?.toPathSequence()
                testCounter.getTimeCounter("total").recordNanos(results2.first)
                testCounter.getTimeCounter("placement $i").recordNanos(results2.first)

                if (path1 == null && path2 == null) {
                    continue
                }

                val failFn = {
                    fail(
                        """
                                File: $json

                                Placements:
                                $log
                                
                                Placement code:
                                $logTowerPlacementCode
                                
                                Paths:
                                path1 is $path1
                                path2 is $path2
                                
                                Path lengths:
                                path1 length: ${path1?.pathLength}
                                path2 length: ${path2?.pathLength}
                                """.removeAllIndents()
                    )
                }

                if (path1 != null && path2 == null) {
                    failFn()
                }

                if (!doublesSimilar(path1!!.pathLength, path2!!.pathLength, 0.5)) {
                    failFn()
                }
            }

            println(
                """
                numVerified: $numVerified
            """.trimIndent()
            )

        }

        jsonFiles.parallelMap {
            runTest(it)
        }

        println(
            "This test ensures that the PathUpdater works as expected and does not deviate " +
                    "from the path if we were to calculate it in it's entirety. It works by " +
                    "generating random placements (up to $numRandomPlacements) and comparing path " +
                    "results from placing the tower using the PathUpdater and from placing the tower " +
                    "into the game map and recalculating the entire path."
        )
        println(baseCounter)
        println(testCounter)
        Unit
    }

    @Test
    @Ignore
    fun correctnessRegressionTest_initialization() = runBlockingNoJs {
        val searcher1 = AStarSearcher

        val jsonFiles = GOLDENS_DATA_VFS().listSimple()

        val expectedRegressions = setOf<String>(
            "sadfsaf",
            //            "01600.json",
            //            "01748.json",
            //            "00646.json"
        )

        val counters = StatCounterMap("Regression Test Counter")

        var numVerified = 0
        for ((i, json) in jsonFiles.withIndex()) {
            if (expectedRegressions.contains(json.baseName)) continue
            println("${json.baseName}: verifying $json")
            numVerified++
            val jsonString = json.readString()

            val gameMap = Json.decodeFromString<GameMap>(jsonString)

            val results1 = measureTime { PathFinder.getShortestPath(gameMap, searcher1) }
            val path1 = results1.second
            counters.getTimeCounter("base time").recordNanos(results1.first)

            val results2 = measureTime { PathUpdater(gameMap).gamePath }
            val path2 = results2.second
            counters.getTimeCounter("test time").recordNanos(results2.first)

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
}