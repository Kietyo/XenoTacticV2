package solver

import com.soywiz.kds.Array2
import com.soywiz.kds.PriorityQueue
import com.soywiz.kds.iterators.parallelMap
import com.soywiz.korma.geom.Rectangle
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder
import utils.TowerCache
import kotlin.math.sign

fun Array2<Boolean>.toIntPointCollection(): Collection<IntPoint> {
    val points = mutableListOf<IntPoint>()
    this.each { x, y, v ->
        if (v) {
            points.add(IntPoint(x, y))
        }
    }
    return points
}

object StandardSolver4 : Solver {
    override fun solve(map: GameMap, solverParams: SolverParams): SolverResult {
        return StandardSolverInternal(map, solverParams).solve()
    }

    data class SearchState4(val towerPlacements: Array2<Boolean>) {
        fun satisfiesSolverParams(solverParams: SolverParams): Boolean {
            val numTowers = towerPlacements.count { it }
            require(numTowers <= solverParams.maxTowers)
            return numTowers == solverParams.maxTowers
        }
    }

    data class SearchResult4(
        val bestTowerPlacements: Array2<Boolean>,
        val pathSequence: PathSequence,
        val pathLength: Double
    )

    class StandardSolverInternal(
        val map: GameMap,
        val solverParams: SolverParams,
    ) {
        val availableTowerPlacementSpots: List<IntPoint> = getAvailableTowerPlacementSpots(map)
        var towerCache = TowerCache(map.width, map.height)
        val _towerPlacementsToPathCache = TowerPlacementsToPathCache()

        fun solve(): SolverResult {
            require(solverParams.optimizationGoal is OptimizationGoal.MaxPath)

            PathFinder.getShortestPath(map) ?: return SolverResult.Unsuccessful

            val result = solveInternal(
                SearchState4(Array2(map.width, map.height, false))
            ) ?: return SolverResult.Unsuccessful
            println(
                """
                numTotalSolverInternalCalls: $numTotalSolverInternalCalls
                numStatesExplored: $numStatesExplored
            """.trimIndent()
            )

            val towerPlacementSet = mutableSetOf<IntPoint>()
            result.bestTowerPlacements.each { x, y, v ->
                if (v) {
                    towerPlacementSet.add(IntPoint(x, y))
                }
            }

            return SolverResult.Success(
                SearchResult.Success(
                    SearchState(towerPlacementSet),
                    result.pathSequence
                )
            )
        }

        // Map of current search state to the best tower placement spots
        val dp = mutableMapOf<SearchState4, SearchResult4?>()
        var numTotalSolverInternalCalls = 0
        var numStatesExplored = 0
        var numCacheHits = 0

        private fun solveInternal(state: SearchState4): SearchResult4? {
            numTotalSolverInternalCalls++
            if (dp.containsKey(state)) {
                numCacheHits++
                if (numCacheHits % 1000 == 0) {
                    println("Num cache hits: $numCacheHits")
                }
                return dp[state]
            }
            numStatesExplored++
            if (numStatesExplored % 1000 == 0) {
                println("Num states explored: $numStatesExplored")
            }
            val path = _towerPlacementsToPathCache.getShortestPath(
                map,
                state.towerPlacements,
                towerCache
            )
            if (path == null) {
                dp[state] = null
                return null
            }

            if (state.satisfiesSolverParams(solverParams)) {
                val result = SearchResult4(state.towerPlacements, path, path.pathLength)
                dp[state] = result
                return result
            }

            var maxPathLength = path.pathLength
            var maxSearchResult = SearchResult4(state.towerPlacements, path, path.pathLength)

            for (spot in getNextTowerPlacementSpots(
                map, state.towerPlacements.toIntPointCollection(), path,
                availableTowerPlacementSpots, towerCache
            )) {
                if (!path.intersectsRectangle(Rectangle(spot.x, spot.y, 2, 2))) {
                    continue
                }
                val clone = state.towerPlacements.clone()
                clone[spot.x, spot.y] = true
                val result = solveInternal(
                    SearchState4(clone)
                ) ?: continue
                if (result.pathLength > maxPathLength) {
                    maxPathLength = result.pathLength
                    maxSearchResult = result
                }
            }

            dp[state] = maxSearchResult

            return maxSearchResult
        }
    }
}