package utils

import com.xenotactic.gamelogic.model.MapEntity

class TowerCache {
    val towers = mutableMapOf<Int, MutableMap<Int, MapEntity.Tower>>()

    fun getTower(x: Int, y: Int): MapEntity.Tower {
        val tower = towers.getOrPut(x) {
            mutableMapOf<Int, MapEntity.Tower>()
        }.getOrPut(y) {
            MapEntity.Tower(x, y)
        }
        return tower
    }
}