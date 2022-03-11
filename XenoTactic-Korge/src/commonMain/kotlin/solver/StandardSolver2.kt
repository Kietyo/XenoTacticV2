package solver

import com.soywiz.kds.PriorityQueue
import model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import pathing.PathFinder
import utils.TowerCache
import kotlin.math.sign

object StandardSolver2 : Solver {
    override fun solve(map: GameMap, solverParams: SolverParams): SolverResult {
        return StandardSolverInternal(map, solverParams).solve()
    }

    class StandardSolverInternal(val _map: GameMap, val _solverParams: SolverParams) {
        var _towerCache = TowerCache()
        val _towerPlacementsToPathCache = TowerPlacementsToPathCache()

        fun solve(): SolverResult {
            require(_solverParams.optimizationGoal is OptimizationGoal.MaxPath)

            PathFinder.getShortestPath(_map) ?: return SolverResult.Unsuccessful

            val availableTowerPlacementSpots = getAvailableTowerPlacementSpots(_map)

            val result = solveInternal(SearchState(emptySet()), availableTowerPlacementSpots)
            println(
                """
                numTotalSolverInternalCalls: $numTotalSolverInternalCalls
                numStatesExplored: $numStatesExplored
            """.trimIndent()
            )
            return when (result) {
                is SearchResult.Success -> {
                    SolverResult.Success(result)
                }
                SearchResult.Unsuccessful -> SolverResult.Unsuccessful
            }
        }

        val dp = mutableMapOf<SearchState, SearchResult>()
        var numTotalSolverInternalCalls = 0
        var numStatesExplored = 0

        fun solveInternal(state: SearchState, availableTowerPlacementSpots: List<IntPoint>):
                SearchResult {
            numTotalSolverInternalCalls++
            if (dp.containsKey(state)) return dp[state]!!
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

            val pointsOrderedByPathLength = PriorityQueue<Pair<Double, IntPoint>> { o1, o2 ->
                (o2.first - o1.first).sign.toInt()
            }

            for (spot in getNextTowerPlacementSpots(
                _map, state.towerPlacements, path,
                availableTowerPlacementSpots, _towerCache
            )) {
                val pathWithSpot = _towerPlacementsToPathCache.getShortestPath(
                    _map,
                    state.towerPlacements + spot,
                    _towerCache
                )
                    ?: continue
                pointsOrderedByPathLength.add(Pair(pathWithSpot.pathLength, spot))
            }

            var bestResult: SearchResult = SearchResult.Success(state, path)
            var bestPathLength = path.pathLength
            var numProcessed = 0
            for (pair in pointsOrderedByPathLength) {
                if (numProcessed > 1) {
                    break
                }
                numProcessed++
                val result = solveInternal(
                    SearchState(state.towerPlacements + pair.second),
                    availableTowerPlacementSpots
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