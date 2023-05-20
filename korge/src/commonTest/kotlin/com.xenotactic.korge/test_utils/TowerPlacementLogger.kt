package com.xenotactic.korge.test_utils

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity

class TowerPlacementLogger {
    var towerPlacements = mutableListOf<GameUnitTuple>()

    fun logTowerPlacement(tower: MapEntity.Tower) {
        towerPlacements.add(GameUnitTuple(tower.x, tower.y))
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