package test_utils

import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity

class TowerPlacementLogger {
    var towerPlacements = mutableListOf<IntPoint>()

    fun logTowerPlacement(tower: MapEntity.Tower) {
        towerPlacements.add(IntPoint(tower.x, tower.y))
    }

    fun toTowerPlacementCodeString(): String {
        val sb = StringBuilder()
        sb.appendLine("Tower placements:")
        for (placement in towerPlacements) {
            sb.appendLine("MapEntity.Tower(${placement.x}, ${placement.x}),")
        }
        return sb.toString()
    }
}