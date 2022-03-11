package solver

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.globals.COUNTERS
import loadGameMapFromGoldensBlocking
import model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.utils.measureTime
import pathing.AStarSearcher
import random.MapGeneratorConfiguration
import random.MapGeneratorResult
import random.RandomMapGenerator
import testdata.STANDARD_MAP

import kotlin.random.Random
import kotlin.test.*

internal class SolverKtTest {

    @Test
    fun getPathWithTowersAtSpots() {
        val map = STANDARD_MAP
        val mapBefore = map.copy()

        val towerPlacementsToPathCache = TowerPlacementsToPathCache()

        assertNotNull(towerPlacementsToPathCache.getShortestPath(map, setOf(IntPoint(0, 4))))
        assertEquals(map, mapBefore)
    }

    @Test
    fun solverTest() {

        val solver = StandardSolver2

        val result = measureTime("Time it took to solve") {
            solver.solve(STANDARD_MAP, SolverParams(15, OptimizationGoal.MaxPath))
        }

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }

    @Test
    @Ignore
    fun solverTest2() {
        val map: GameMap
        while (true) {
            val genMap = RandomMapGenerator.generate(
                MapGeneratorConfiguration(
                    Random.nextLong(),
                    30, 20,
                    6,
                    20,
                    0,
                    AStarSearcher,
                    1000,
                )
            )
            when (genMap) {
                is MapGeneratorResult.Success -> {
                    map = genMap.map
                    break
                }
                is MapGeneratorResult.Failure -> continue
            }
        }

        println("Generated map:")
        println(map)

        val solver = StandardSolver2

        println("Attempting to solve:")
        val result = measureTime("Time it took to solve") {
            solver.solve(map, SolverParams(10, OptimizationGoal.MaxPath))
        }

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }

    @Test
    fun solverTest3() {
        Logger.defaultLevel = Logger.Level.INFO
        val map: GameMap
        while (true) {
            val genMap = RandomMapGenerator.generate(
                MapGeneratorConfiguration(
                    Random.nextLong(),
                    30, 20,
                    6,
                    20,
                    0,
                    AStarSearcher,
                    1000,
                )
            )
            when (genMap) {
                is MapGeneratorResult.Success -> {
                    map = genMap.map
                    break
                }
                is MapGeneratorResult.Failure -> continue
            }
        }

        println("Generated map:")
        println(map)

        val solver = StandardSolver3()

        println("Attempting to solve:")
        val result = measureTime("Time it took to solve") {
            solver.solve(map, SolverParams(6, OptimizationGoal.MaxPath))
        }

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }

    @Test
    fun regressionTest1() {
        Logger.defaultLevel = Logger.Level.INFO
        val map: GameMap = loadGameMapFromGoldensBlocking("00760.json")

        val solver = StandardSolver3(
            SolverSettings(
                numSpotsToConsider = 1,
                numSpotsToExplore = 1
            )
        )

        println("Attempting to solve:")
        val result = measureTime("Time it took to solve") {
            solver.solve(map, SolverParams(15, OptimizationGoal.MaxPath))
        }

        assertIs<SolverResult.Success>(result.second)

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }
}