package com.xenotactic.korge.model

import com.xenotactic.gamelogic.utils.loadGameMapFromGoldenBlocking
import pathing.PathFinder
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class PathFinderTest {
    @Test
    fun getShortestPathOnPathingPoints() {
        val gameMap = loadGameMapFromGoldenBlocking("00019.json")
        val teleportPair = gameMap.teleportPairs.first {
            it.sequenceNumber == 1
        }
        assertNotNull(
            PathFinder.getShortestPathOnPathingPoints(
                gameMap,
                listOf(
                    gameMap.getStart()!!,
                    teleportPair.teleportOut
                ),
                teleportPairs = emptyList()
            )
        )
    }
}