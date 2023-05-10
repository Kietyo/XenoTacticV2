package utils

import korlibs.datastructure.Array2
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

class TowerCache(width: Int, height: Int) {
    private val towerPool = Array2.withGen(width, height) { x, y ->
        MapEntity.Tower(x.toGameUnit(), y.toGameUnit())
    }

    fun getTower(x: GameUnit, y: GameUnit): MapEntity.Tower {
        return getTower(x.toInt(), y.toInt())
    }

    fun getTower(x: Int, y: Int): MapEntity.Tower {
        return towerPool[x, y]
    }
}