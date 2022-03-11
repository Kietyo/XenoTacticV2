package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity

fun getAvailableTowerPlacementSpots(map: GameMap): List<IntPoint> {
    val availableSpots = mutableListOf<IntPoint>()
    val pathingEntities = map.getPathingEntities()
    for (i in 0..(map.width - 2)) {
        for (j in 0..(map.height - 2)) {
            val candidate = MapEntity.Tower(i, j)
            if (map.intersectsBlockingEntities(candidate)) {
                continue
            }
            if (pathingEntities.any { it.isFullyCoveredBy(candidate) }) {
                continue
            }
            availableSpots.add(candidate.intPoint)
        }
    }
    return availableSpots
}