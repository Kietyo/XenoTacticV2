package solver

import korlibs.datastructure.Array2
import korlibs.datastructure.each
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder

import utils.TowerCache

class TowerPlacementsToPathCache {
    val cache = mutableMapOf<Set<GameUnitTuple>, PathSequence?>()

    fun getShortestPath(
        map: GameMap, placementSpots: Array2<Boolean>,
        towerCache: TowerCache
    ): PathSequence? {
        val spots = mutableSetOf<GameUnitTuple>()
        placementSpots.each { x, y, v ->
            if (v) {
                spots.add(GameUnitTuple(x, y))
            }
        }
        return getShortestPath(map, spots, towerCache)
    }

    fun getShortestPath(
        map: GameMap, placementSpots: Set<GameUnitTuple>,
        towerCache: TowerCache = TowerCache(map.width.toInt(), map.height.toInt())
    ): PathSequence? {
        return cache.getOrPut(placementSpots) {
            PathFinder.getShortestPathWithTowers(map, placementSpots.map {
                towerCache.getTower(it.x.toInt(), it.y.toInt())
            })
        }
    }
}