package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.toGameUnit

fun getAvailableTowerPlacementSpots(map: GameMap): List<GameUnitTuple> {
    val availableSpots = mutableListOf<GameUnitTuple>()
    val pathingEntities = map.getPathingEntities()
    for (i in 0..(map.width.toInt() - 2)) {
        for (j in 0..(map.height.toInt() - 2)) {
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