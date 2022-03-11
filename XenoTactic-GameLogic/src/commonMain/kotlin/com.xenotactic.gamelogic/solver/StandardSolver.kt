package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder

import utils.TowerCache

object StandardSolver : Solver {
    override fun solve(map: GameMap, solverParams: SolverParams): SolverResult {
        return StandardSolverInternal(map, solverParams).solve()
    }

    class StandardSolverInternal(val _map: GameMap, val _solverParams: SolverParams) {

        val _cachedPaths = mutableMapOf<SearchState, PathSequence?>()
        val _towerCache = TowerCache()
        val _placementsToPathCache = TowerPlacementsToPathCache()

        fun solve(): SolverResult {
            require(_solverParams.optimizationGoal is OptimizationGoal.MaxPath)

            PathFinder.getShortestPath(_map) ?: return SolverResult.Unsuccessful

            val availableTowerPlacementSpots = getAvailableTowerPlacementSpots(_map)

            val result = solveInternal(SearchState(emptySet()), availableTowerPlacementSpots)
            println("""
                numTotalSolverInternalCalls: $numTotalSolverInternalCalls
                numStatesExplored: $numStatesExplored
            """.trimIndent())
            return when (result) {
                is SearchResult.Success -> {
                    SolverResult.Success(result)
                }
                SearchResult.Unsuccessful -> SolverResult.Unsuccessful
            }
        }

//        fun solve2(): SolverResult {
//            require(solverParams.optimizationGoal is OptimizationGoal.MaxPath)
//
//            val currentPath = map.getShortestPath() ?: return SolverResult.Unsuccessful
//
//            val availableTowerPlacementSpots = getAvailableTowerPlacementSpots(map)
//
//            println()
//
//            val queue = LinkedList<SearchState>()
//            for (spot in getNextTowerPlacementSpots(
//                map, currentPath,
//                availableTowerPlacementSpots
//            )) {
//                queue.push(SearchState(setOf(spot)))
//            }
//
//            val finishedStates = mutableSetOf<SearchState>()
//
//            var bestResult: SearchResult? = null
//            var bestPathLength = Float.MIN_VALUE
//
//            var numStatesSearched = 0
//            while (queue.size > 0) {
//                numStatesSearched++
//                val currentSearchState = queue.pop()
//                if (currentSearchState.satisfiesSolverParams(solverParams) &&
//                    finishedStates.contains(currentSearchState)
//                ) {
//                    // We already calculate the path for this, so can skip.
//                    continue
//                }
//
//                val path = getPathForSearchState(currentSearchState)
//                //                println("""
//                //                    queue: $queue,
//                //                    currentSearchState: $currentSearchState,
//                //                    currentSearchState.numTowers: ${currentSearchState.numTowers},
//                //                    path: $path
//                //                """.trimIndent())
//                if (path == null) {
//                    finishedStates.add(currentSearchState)
//                    continue
//                }
//                if (currentSearchState.satisfiesSolverParams(solverParams)) {
//                    finishedStates.add(currentSearchState)
//                    val pathLength = path.pathLength
//                    if (pathLength > bestPathLength) {
//                        bestPathLength = pathLength
//                        bestResult = SearchResult(currentSearchState, path)
//                    }
//                    continue
//                }
//
//                for (spot in getNextTowerPlacementSpots(map, path, availableTowerPlacementSpots)) {
//                    queue.push(SearchState(currentSearchState.towerPlacements + spot))
//                }
//            }
//
//            println(
//                """
//                Number results: ${finishedStates.size},
//                States searched: $numStatesSearched
//            """.trimIndent()
//            )
//
//            if (bestResult == null) return SolverResult.Unsuccessful
//            return SolverResult.Success(bestResult)
//        }

        val dp = mutableMapOf<SearchState, SearchResult>()
        var numTotalSolverInternalCalls = 0
        var numStatesExplored = 0

        fun solveInternal(state: SearchState, availableTowerPlacementSpots: List<IntPoint>):
                SearchResult {
            numTotalSolverInternalCalls++
            if (dp.containsKey(state)) return dp[state]!!
            numStatesExplored++
            println("numStatesExplored: $numStatesExplored")
            val path = _placementsToPathCache.getShortestPath(_map, state.towerPlacements, _towerCache)
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

            var bestResult: SearchResult = SearchResult.Unsuccessful
            var bestPathLength = Double.MIN_VALUE
            for (spot in getNextTowerPlacementSpots(_map, state.towerPlacements, path,
                availableTowerPlacementSpots, _towerCache)) {
                val result = solveInternal(
                    SearchState(state.towerPlacements + spot),
                    availableTowerPlacementSpots)
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