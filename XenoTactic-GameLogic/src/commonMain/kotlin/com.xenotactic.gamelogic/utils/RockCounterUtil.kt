package com.xenotactic.gamelogic.utils

import korlibs.datastructure.IntArray2
import com.xenotactic.gamelogic.model.GameMap

object RockCounterUtil {
    fun calculate(gameMap: GameMap): IntArray2 {
        val result = IntArray2(gameMap.width.toInt(), gameMap.height.toInt(), 0)

        repeat(result.width) { x ->
            repeat(result.height) { y ->
                val allRocks = gameMap.getAllRocksAtPoint(x.toGameUnit(), y.toGameUnit())
                result[x, y] = allRocks.count()
            }
        }

        return result
    }
}