package pathing

import com.xenotactic.gamelogic.utils.loadGameMapFromGoldenBlocking
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.utils.toGameUnit
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

    @Test
    fun regressionTest1() {
        println(
            PathFinder.getUpdatablePath(
                30.toGameUnit(),
                11.toGameUnit(),
                start = RectangleEntity(22.toGameUnit(), 0.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()),
                finish = RectangleEntity(3.toGameUnit(), 2.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()),
                blockingEntities = listOf(
                    RectangleEntity(22.toGameUnit(), 6.toGameUnit(), 2.toGameUnit(), 4.toGameUnit()),
                    RectangleEntity(10.toGameUnit(), 3.toGameUnit(), 4.toGameUnit(), 2.toGameUnit()),
                    RectangleEntity(20.toGameUnit(), 0.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()),
                ),
                pathingEntities = listOf(),
                teleportPairs = listOf()
            )
        )
    }
}