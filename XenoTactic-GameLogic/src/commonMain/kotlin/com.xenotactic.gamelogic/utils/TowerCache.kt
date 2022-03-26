package utils

import com.soywiz.kds.Array2
import com.xenotactic.gamelogic.model.MapEntity

class TowerCache(width: Int, height: Int) {
    private val towerPool = Array2.withGen(width, height) { x, y ->
        MapEntity.Tower(x, y)
    }

    fun getTower(x: Int, y: Int): MapEntity.Tower {
        return towerPool[x, y]
    }
}