package pathing

import com.xenotactic.gamelogic.test_utils.loadGameMapFromGoldensBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class PathFinderTest {
    @Test
    fun getShortestPathOnPathingPoints() {
        val gameMap = loadGameMapFromGoldensBlocking("00019.json")
        val teleportPair = gameMap.teleportPairs.first {
            it.teleportOut.sequenceNumber == 1
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