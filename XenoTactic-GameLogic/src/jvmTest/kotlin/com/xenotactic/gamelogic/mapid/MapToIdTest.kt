package com.xenotactic.gamelogic.mapid

import com.soywiz.kds.iterators.parallelMap
import getAllGoldenMaps
import loadGameMapFromGoldensBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


internal class MapToIdTest {

    @Test
    fun calculateIdWorks() {
        val gameMap = loadGameMapFromGoldensBlocking("00001.json")

        assertEquals(MapToId.calculateId(gameMap), "52d47afd374cdcb108647785c5ffad999a1d5942")
    }

    @Test
    fun regressionTestForGoldenMaps() {
        val gameMaps = getAllGoldenMaps()

        val resultSet = mutableSetOf<String>()

        val result = gameMaps.mapTo(resultSet) {
            MapToId.calculateId(it)
        }

        assertEquals(gameMaps.size, resultSet.size)

        println(resultSet)
    }
}