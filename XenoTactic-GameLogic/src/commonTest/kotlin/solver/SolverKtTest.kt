package solver

import korlibs.logger.Logger
import com.xenotactic.gamelogic.utils.COUNTERS
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.random.MapGeneratorConfiguration
import com.xenotactic.gamelogic.random.MapGeneratorResult
import com.xenotactic.gamelogic.random.RandomMapGenerator
import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.gamelogic.utils.toGameUnit
import pathing.AStarSearcher
import test_utils.STANDARD_MAP
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class SolverKtTest {

    @Test
    fun getPathWithTowersAtSpots() {
        val map = STANDARD_MAP
        val mapBefore = map.copy()

        val towerPlacementsToPathCache = TowerPlacementsToPathCache()

        assertNotNull(towerPlacementsToPathCache.getShortestPath(map, setOf(GameUnitTuple(0, 4))))
        assertEquals(map, mapBefore)
    }

    @Test
    fun solverTest() {
        val solver = StandardSolver3()

        val result = measureTime("Time it took to solve") {
            solver.solve(STANDARD_MAP, SolverParams(15, OptimizationGoal.MaxPath))
        }

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }

    @Test
    fun solver3Test2() {
        val map: GameMap
        while (true) {
            val genMap = RandomMapGenerator.generate(
                MapGeneratorConfiguration(
                    Random.nextLong(),
                    30.toGameUnit(), 20.toGameUnit(),
                    6,
                    20,
                    0,
                    searcher = AStarSearcher,
                    failureAfterTotalAttempts = 1000,
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

        val solver = StandardSolver3(SolverSettings(2, 2))

        println("Attempting to solve:")
        val result = measureTime("Time it took to solve") {
            solver.solve(map, SolverParams(10, OptimizationGoal.MaxPath))
        }

        println(result)
        println("Time in seconds: ${result.first / 1e9}")

        println(COUNTERS)
    }

    @Test
    fun solver3Test3() {
        Logger.defaultLevel = Logger.Level.INFO
        val map: GameMap
        while (true) {
            val genMap = RandomMapGenerator.generate(
                MapGeneratorConfiguration(
                    Random.nextLong(),
                    30.toGameUnit(), 20.toGameUnit(),
                    6,
                    20,
                    0,
                    searcher = AStarSearcher,
                    failureAfterTotalAttempts = 1000,
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


}