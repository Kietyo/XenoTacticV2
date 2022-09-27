package solver

import com.soywiz.kds.Array2
import com.soywiz.kds.each
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitPoint
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder

import utils.TowerCache

class TowerPlacementsToPathCache {
    val cache = mutableMapOf<Set<GameUnitPoint>, PathSequence?>()

    fun getShortestPath(
        map: GameMap, placementSpots: Array2<Boolean>,
        towerCache: TowerCache
    ): PathSequence? {
        val spots = mutableSetOf<GameUnitPoint>()
        placementSpots.each { x, y, v ->
            if (v) {
                spots.add(GameUnitPoint(x, y))
            }
        }
        return getShortestPath(map, spots, towerCache)
    }

    fun getShortestPath(
        map: GameMap, placementSpots: Set<GameUnitPoint>,
        towerCache: TowerCache = TowerCache(map.width, map.height)
    ): PathSequence? {
        return cache.getOrPut(placementSpots) {
            PathFinder.getShortestPathWithTowers(map, placementSpots.map {
                towerCache.getTower(it.x, it.y)
            })
        }
    }
}