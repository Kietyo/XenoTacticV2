package com.xenotactic.gamelogic.mapid

import com.soywiz.kds.iterators.parallelMap
import com.soywiz.korio.lang.assert
import com.xenotactic.gamelogic.korge_utils.getAllGoldenMaps
import com.xenotactic.gamelogic.korge_utils.loadGameMapFromGoldenBlocking
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.test_utils.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MapToIdTest {

    @Test
    fun calculateIdWorks() {
        val gameMap = loadGameMapFromGoldenBlocking("00001.json")

        assertEquals(MapToId.calculateId(gameMap), "52d47afd374cdcb108647785c5ffad999a1d5942")
    }

    @Test
    fun regressionTestForGoldenMaps() {
        val UPDATE_GOLDENS = false
        val expectedMapIds = getGoldenMapIds()

        println(expectedMapIds)

        val gameMaps = getAllGoldenMaps()
        val idToGameMapResults = mutableMapOf<String, GameMap>()
        gameMaps.parallelMap {
            idToGameMapResults.put(MapToId.calculateId(it), it)
        }

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
            "Found a bad test case(s):\n${badTestCases.joinToString("\n")}"
        }
    }
}