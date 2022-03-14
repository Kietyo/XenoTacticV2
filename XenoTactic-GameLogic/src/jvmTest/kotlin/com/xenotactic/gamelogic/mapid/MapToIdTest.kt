package com.xenotactic.gamelogic.mapid

import MapIds
import com.soywiz.kds.iterators.parallelMap
import com.xenotactic.gamelogic.model.GameMap
import getAllGoldenMaps
import getGoldenMapIds
import loadGameMapFromGoldensBlocking
import writeGoldenMapIds
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
        val UPDATE_GOLDENS = true
        val expectedMapIds = getGoldenMapIds()

        println(expectedMapIds)

        val gameMaps = getAllGoldenMaps()
        val idToGameMapResults = mutableMapOf<String, GameMap>()
        gameMaps.parallelMap {
            idToGameMapResults.put(MapToId.calculateId(it), it)
        }

//        assertEquals(gameMaps.size, idToGameMapResults.size)

        if (UPDATE_GOLDENS) {
            writeGoldenMapIds(MapIds.create(idToGameMapResults.keys))
        } else {
            assertMapIdEquals(expectedMapIds.ids, idToGameMapResults)
        }
    }

    data class BadTestCases(
        val idNotExpected: String,
        val gameMap: GameMap
    )

    fun assertMapIdEquals(expectedIds: List<String>, mapAndIds: Map<String, GameMap>) {
        assertEquals(expectedIds.size, mapAndIds.size)

        val expectedIdSet = expectedIds.toSet()

        val badTestCases = mutableListOf<BadTestCases>()

        for ((mapId, gameMap) in mapAndIds) {
            if (mapId !in expectedIdSet) {
                badTestCases += BadTestCases(mapId, gameMap)
            }
        }

        assert(badTestCases.isEmpty()) {
            "Found a lot of bad test cases:\n${badTestCases.joinToString("\n")}"
        }
    }
}