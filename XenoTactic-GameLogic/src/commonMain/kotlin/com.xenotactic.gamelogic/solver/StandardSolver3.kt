package solver

import korlibs.datastructure.PriorityQueue
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitTuple
import pathing.PathFinder
import utils.TowerCache
import kotlin.math.sign

data class SolverSettings(
    val numSpotsToConsider: Int = 1,
    val numSpotsToExplore: Int = 1
)

class StandardSolver3(val solverSettings: SolverSettings = SolverSettings()) : Solver {
    override fun solve(map: GameMap, solverParams: SolverParams): SolverResult {
        return StandardSolverInternal(solverSettings, map, solverParams).solve()
    }

    class StandardSolverInternal(
        val _solverSettings: SolverSettings,
        val _map: GameMap,
        val _solverParams: SolverParams
    ) {
        val _availableTowerPlacementSpots = getAvailableTowerPlacementSpots(_map)
        var _towerCache = TowerCache(_map.width.toInt(), _map.height.toInt())
        val _towerPlacementsToPathCache = TowerPlacementsToPathCache()

        var numTotalSolverInternalCalls = 0
        var numStatesExplored = 0
        var numCacheHits = 0

        fun solve(): SolverResult {
            require(_solverParams.optimizationGoal is OptimizationGoal.MaxPath)

            val initialShortestPath = PathFinder.getShortestPath(_map)

            initialShortestPath ?: return SolverResult.Unsuccessful

            val result = solveInternal(SearchState(emptySet()))
            println(
                """
                numTotalSolverInternalCalls: $numTotalSolverInternalCalls
                numStatesExplored: $numStatesExplored
                numCacheHits: $numCacheHits
            """.trimIndent()
            )
            return when (result) {
                is SearchResult.Success -> {
                    println("""
                        Initial path length: ${initialShortestPath.pathLength}
                        Path length of found solver: ${result.pathSequence.pathLength}
                    """.trimIndent())
                    SolverResult.Success(result)
                }
                SearchResult.Unsuccessful -> SolverResult.Unsuccessful
            }
        }

        val dp = mutableMapOf<SearchState, SearchResult>()

        fun solveInternal(state: SearchState):
                SearchResult {
            numTotalSolverInternalCalls++
            if (dp.containsKey(state)) {
                numCacheHits++
                return dp[state]!!
            }
            numStatesExplored++
            val path = _towerPlacementsToPathCache.getShortestPath(
                _map,
                state.towerPlacements,
                _towerCache
            )
            if (path == null) {
                val result = SearchResult.Unsuccessful
                dp[state] = result
                return result
            }
            if (state.satisfiesSolverParams(_solverParams)) {
                val result = SearchResult.Success(state, path)
                dp[state] = result
                return result
            }

            val pointsOrderedByPathLength = PriorityQueue<Pair<Double, GameUnitTuple>> { o1, o2 ->
                (o2.first - o1.first).sign.toInt()
            }

            var numSpotsConsidered = 0
            for (spot in getNextTowerPlacementSpotsV2(
                _map, state.towerPlacements, path,
                _availableTowerPlacementSpots, _towerCache
            ).neighborBlockingEntities) {
                val pathWithSpot = _towerPlacementsToPathCache.getShortestPath(
                    _map,
                    state.towerPlacements + spot,
                    _towerCache
                )
                    ?: continue
                pointsOrderedByPathLength.add(Pair(pathWithSpot.pathLength.toDouble(), spot))
                numSpotsConsidered++
                if (numSpotsConsidered >= _solverSettings.numSpotsToConsider) {
                    break
                }
            }

            var bestResult: SearchResult = SearchResult.Success(state, path)
            var bestPathLength = path.pathLength
            var numProcessed = 0
            for (pair in pointsOrderedByPathLength) {
                if (numProcessed > _solverSettings.numSpotsToExplore) {
                    break
                }
                numProcessed++
                val result = solveInternal(
                    SearchState(state.towerPlacements + pair.second)
                )
                when (result) {
                    is SearchResult.Success -> {
                        val pathLength = result.pathSequence.pathLength
                        if (pathLength > bestPathLength) {
                            bestResult = result
                            bestPathLength = pathLength
                        }
                    }
                    SearchResult.Unsuccessful -> continue
                }
            }

            dp[state] = bestResult
            return bestResult
        }
    }
}