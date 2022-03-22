package com.xenotactic.gamelogic.utils

import com.soywiz.kds.Array2
import com.soywiz.kds.IntArray2
import com.xenotactic.gamelogic.model.GameMap

object RockCounterUtil {
    fun calculate(gameMap: GameMap): IntArray2 {
        val result = IntArray2(gameMap.width, gameMap.height, 0)

        repeat(gameMap.width) { x ->
            repeat(gameMap.height) { y ->
                val allRocks = gameMap.getAllRocksAtPoint(x, y)
                result[x, y] = allRocks.count()
            }
        }

        return result
    }
}