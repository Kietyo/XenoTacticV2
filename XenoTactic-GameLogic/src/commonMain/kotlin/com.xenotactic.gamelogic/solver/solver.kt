package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IRectangle
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.EntitiesBlockingEntityUtil
import com.xenotactic.gamelogic.utils.MapBlockingUtil
import com.xenotactic.gamelogic.utils.toGameUnit
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
    val towerPlacements: Set<GameUnitTuple>
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
    currentPlacements: Set<GameUnitTuple>,
    pathSequence: PathSequence,
    towerPlacementSpots: List<GameUnitTuple>,
    towerCache: TowerCache
): List<GameUnitTuple> {
    for (spot in currentPlacements) map.placeEntity(towerCache.getTower(spot.x, spot.y))

    val result = towerPlacementSpots.filter {
        pathSequence.intersectsRectangle(IRectangle(it.x.value.toDouble(), it.y.value.toDouble(), 2.0, 2.0)) &&
                !map.intersectsBlockingEntities(it.x, it.y, 2.toGameUnit(), 2.toGameUnit())
    }

    for (spot in currentPlacements) map.removeEntity(towerCache.getTower(spot.x, spot.y))

    return result
}

data class NextTowerPlacementSpotsOutput(
    // Points which neighbors a blocking entity or is touching the map walls on the sides
    val neighborBlockingEntities: Set<GameUnitTuple>,
    // Any remaining spots that are not categorized to the sets above.
    val otherPlacementSpots: Set<GameUnitTuple>
)

fun getNextTowerPlacementSpotsV2(
    map: GameMap,
    currentPlacements: Set<GameUnitTuple>,
    pathSequence: PathSequence,
    towerPlacementSpots: List<GameUnitTuple>,
    towerCache: TowerCache
): NextTowerPlacementSpotsOutput {
    val neighborsBlockingEntities = mutableSetOf<GameUnitTuple>()
    val otherPlacementSpots = mutableSetOf<GameUnitTuple>()
    for (spot in currentPlacements) map.placeEntity(towerCache.getTower(spot.x, spot.y))

    for (spot in towerPlacementSpots) {
        val tower = towerCache.getTower(spot.x, spot.y)
        val intersectsPath = pathSequence
            .intersectsRectangle(IRectangle(spot.x.value.toDouble(), spot.y.value.toDouble(), 2.0, 2.0))
        if (intersectsPath
            && !map.intersectsBlockingEntities(spot.x, spot.y, 2.toGameUnit(), 2.toGameUnit())
        ) {
            if (EntitiesBlockingEntityUtil(tower, map).anyPartiallyBlocking ||
                MapBlockingUtil(tower, map.width.toInt(), map.height.toInt()).hasBlockingSide
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