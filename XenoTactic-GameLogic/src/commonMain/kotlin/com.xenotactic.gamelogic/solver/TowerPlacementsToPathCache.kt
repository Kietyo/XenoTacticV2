package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder

import utils.TowerCache

class TowerPlacementsToPathCache {
    val cache = mutableMapOf<Collection<IntPoint>, PathSequence?>()

    fun getShortestPath(
        map: GameMap, placementSpots: Collection<IntPoint>,
        towerCache: TowerCache = TowerCache()
    ): PathSequence? {
        if (cache.containsKey(placementSpots)) return cache[placementSpots]

        val result = PathFinder.getShortestPathWithTowers(map, placementSpots.map {
            towerCache
                .getTower(it.x, it.y)
        })
        cache[placementSpots] = result
        return result
    }
}