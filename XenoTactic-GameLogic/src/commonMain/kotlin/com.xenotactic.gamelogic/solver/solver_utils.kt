package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.toGameUnit

fun getAvailableTowerPlacementSpots(map: GameMap): List<GameUnitPoint> {
    val availableSpots = mutableListOf<GameUnitPoint>()
    val pathingEntities = map.getPathingEntities()
    for (i in 0..(map.width.value - 2)) {
        for (j in 0..(map.height.value - 2)) {
            val candidate = MapEntity.Tower(i.toGameUnit(), j.toGameUnit())
            if (map.intersectsBlockingEntities(candidate)) {
                continue
            }
            if (pathingEntities.any { it.isFullyCoveredBy(candidate) }) {
                continue
            }
            availableSpots.add(candidate.gameUnitPoint)
        }
    }
    return availableSpots
}