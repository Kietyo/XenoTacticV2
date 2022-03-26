package solver

import com.soywiz.kds.Array2
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder

import utils.TowerCache

class TowerPlacementsToPathCache {
    val cache = mutableMapOf<Collection<IntPoint>, PathSequence?>()

    fun getShortestPath(
        map: GameMap, placementSpots: Array2<Boolean>,
        towerCache: TowerCache
    ): PathSequence? {
        val spots = mutableListOf<IntPoint>()
        placementSpots.each { x, y, v ->
            if (v) {
                spots.add(IntPoint(x, y))
            }
        }
        return getShortestPath(map, spots, towerCache)
    }

    fun getShortestPath(
        map: GameMap, placementSpots: Collection<IntPoint>,
        towerCache: TowerCache = TowerCache(map.width, map.height)
    ): PathSequence? {
        if (cache.containsKey(placementSpots)) return cache[placementSpots]

        val result = PathFinder.getShortestPathWithTowers(map, placementSpots.map {
            towerCache.getTower(it.x, it.y)
        })
        cache[placementSpots] = result
        return result
    }
}