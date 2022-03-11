package solver

import com.soywiz.korma.geom.Rectangle
import model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.MapBlockingUtil

import utils.EntitiesBlockingEntityUtil
import utils.TowerCache

sealed class OptimizationGoal {
    object MaxPath : OptimizationGoal()
}

data class SolverParams(
    val maxTowers: Int,
    val optimizationGoal: OptimizationGoal
)

sealed class SolverResult {
    object Unsuccessful : SolverResult()
    data class Success(
        val searchResult: SearchResult.Success
    ) : SolverResult()
}

interface Solver {
    fun solve(map: GameMap, solverParams: SolverParams): SolverResult
}

data class SearchState(
    val towerPlacements: Set<IntPoint>
) {
    val numTowers: Int
        get() = towerPlacements.size

    fun satisfiesSolverParams(solverParams: SolverParams): Boolean {
        require(numTowers <= solverParams.maxTowers)
        return numTowers == solverParams.maxTowers
    }
}

sealed class SearchResult {
    object Unsuccessful : SearchResult()
    data class Success(
        val state: SearchState,
        val pathSequence: PathSequence
    ) : SearchResult()
}

fun getNextTowerPlacementSpots(
    map: GameMap,
    currentPlacements: Collection<IntPoint>,
    pathSequence: PathSequence,
    towerPlacementSpots: List<IntPoint>,
    towerCache: TowerCache
): List<IntPoint> {
    for (spot in currentPlacements) map.placeEntity(towerCache.getTower(spot.x, spot.y))

    val result = towerPlacementSpots.filter {
        pathSequence.intersectsRectangle(Rectangle(it.x.toDouble(), it.y.toDouble(), 2.0, 2.0)) &&
                !map.intersectsBlockingEntities(it.x, it.y, 2, 2)
    }

    for (spot in currentPlacements) map.removeEntity(towerCache.getTower(spot.x, spot.y))

    return result
}

data class NextTowerPlacementSpotsOutput(
    // Points which neighbors a blocking entity or is touching the map walls on the sides
    val neighborBlockingEntities: Set<IntPoint>,
    // Any remaining spots that are not categorized to the sets above.
    val otherPlacementSpots: Set<IntPoint>
)

fun getNextTowerPlacementSpotsV2(
    map: GameMap,
    currentPlacements: Collection<IntPoint>,
    pathSequence: PathSequence,
    towerPlacementSpots: List<IntPoint>,
    towerCache: TowerCache
): NextTowerPlacementSpotsOutput {
    val neighborsBlockingEntities = mutableSetOf<IntPoint>()
    val otherPlacementSpots = mutableSetOf<IntPoint>()
    for (spot in currentPlacements) map.placeEntity(towerCache.getTower(spot.x, spot.y))

    for (spot in towerPlacementSpots) {
        val tower = towerCache.getTower(spot.x, spot.y)
        if (pathSequence
                .intersectsRectangle(Rectangle(spot.x.toDouble(), spot.y.toDouble(), 2.0, 2.0))
            && !map.intersectsBlockingEntities(spot.x, spot.y, 2, 2)
        ) {
            if (EntitiesBlockingEntityUtil(tower, map).anyPartiallyBlocking ||
                MapBlockingUtil(tower, map.width, map.height).hasBlockingSide
            ) {
                neighborsBlockingEntities.add(spot)
            } else {
                otherPlacementSpots.add(spot)
            }
        }
    }

    for (spot in currentPlacements) map.removeEntity(towerCache.getTower(spot.x, spot.y))

    return NextTowerPlacementSpotsOutput(neighborsBlockingEntities, otherPlacementSpots)
}