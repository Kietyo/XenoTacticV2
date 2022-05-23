package utils

import com.soywiz.kds.IntArray2
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.RockCounterUtil
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RockCounterUtilTest {
    @Test
    fun calculateRockCounters1() {
        val gameMap = GameMap.create(
            5, 5,
            MapEntity.ROCK_1X1.at(2, 2)
        )

        val result = RockCounterUtil.calculate(gameMap)

        val expected = IntArray2(5, 5, 0).apply {
            set(2, 2, 1)
        }

        assertEquals(expected, result)
    }

    @Test
    fun calculateRockCounters2() {
        val gameMap = GameMap.create(
            5, 5,
            MapEntity.ROCK_1X1.at(2, 2),
            MapEntity.ROCK_1X1.at(2, 2),
        )

        val result = RockCounterUtil.calculate(gameMap)

        val expected = IntArray2(5, 5, 0).apply {
            set(2, 2, 2)
        }

        assertEquals(expected, result)
    }

    @Test
    fun calculateRockCounters3() {
        val gameMap = GameMap.create(
            5, 5,
            MapEntity.Rock(1, 1, 2, 2),
        )

        val result = RockCounterUtil.calculate(gameMap)

        val expected = IntArray2(5, 5, 0).apply {
            set(1, 1, 1)
            set(1, 2, 1)
            set(2, 1, 1)
            set(2, 2, 1)
        }

        assertEquals(expected, result)
    }

    @Test
    fun calculateRockCounters4() {
        val gameMap = GameMap.create(
            5, 5,
            MapEntity.Rock(1, 1, 2, 2),
            MapEntity.Rock(2, 1, 2, 2),
        )

        val result = RockCounterUtil.calculate(gameMap)

        val expected = IntArray2(5, 5, 0).apply {
            set(1, 1, 1)
            set(1, 2, 1)
            set(2, 1, 2)
            set(2, 2, 2)
            set(3, 1, 1)
            set(3, 2, 1)
        }

        assertEquals(expected, result)
    }
}