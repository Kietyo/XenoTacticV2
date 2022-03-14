package com.xenotactic.gamelogic.mapid

import MapIds
import com.soywiz.kds.iterators.parallelMap
import com.xenotactic.gamelogic.model.GameMap
import getAllGoldenMaps
import getGoldenMapIds
import kotlinx.serialization.Serializable
import loadGameMapFromGoldensBlocking
import writeGoldenMapIds
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class MapToIdTest {

    @Test
    fun calculateIdWorks() {
        val gameMap = loadGameMapFromGoldensBlocking("00001.json")

        assertEquals(MapToId.calculateId(gameMap), "52d47afd374cdcb108647785c5ffad999a1d5942")
    }

    @Test
    fun regressionTestForGoldenMaps() {
        val UPDATE_GOLDENS = false
        val expectedMapIds = getGoldenMapIds()

        println(expectedMapIds)

        val gameMaps = getAllGoldenMaps()
        val resultSet = mutableMapOf<String, GameMap>()
        gameMaps.parallelMap {
            resultSet.put(MapToId.calculateId(it), it)
        }

        assertEquals(gameMaps.size, resultSet.size)

        val setsEqual = expectedMapIds.ids == resultSet.keys

        if (UPDATE_GOLDENS) {
            writeGoldenMapIds(MapIds(resultSet.keys))
        } else {
            assertContentEquals(expectedMapIds.ids, resultSet.keys?.asIterable())
        }
    }
}