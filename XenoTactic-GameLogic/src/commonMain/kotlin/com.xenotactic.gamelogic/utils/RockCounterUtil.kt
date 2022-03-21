package com.xenotactic.gamelogic.utils

import com.soywiz.kds.Array2
import com.xenotactic.gamelogic.model.GameMap

object RockCounterUtil {
    fun calculate(gameMap: GameMap): Array2<Int> {
        val result = Array2<Int>(gameMap.width, gameMap.height, 0)

        repeat(gameMap.width) { x ->
            repeat(gameMap.height) { y ->
                val allRocks = gameMap.getAllRocksAtPoint(x, y)
                result[x, y] = allRocks.count()
            }
        }

        return result
    }
}