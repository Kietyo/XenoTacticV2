package pathing

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class GamePathTest {

    //    @Test
    //    fun teleportsRemainingAt1() {
    //        val gamePath = GamePath(
    //            listOf(
    //                PathSequence(),
    //                PathSequence(),
    //                PathSequence(),
    //                PathSequence(),
    //            ),
    //            listOf(
    //                PathSequenceInfo(
    //                    setOf(0),
    //                ),
    //                PathSequenceInfo(
    //                    setOf(1),
    //                ),
    //                PathSequenceInfo(
    //                    setOf(2),
    //                ),
    //                PathSequenceInfo(
    //                    setOf(3),
    //                )
    //            ),
    //            setOf(0, 1, 2, 3)
    //        )
    //
    //        assertEquals(
    //            setOf(0, 1, 2, 3),
    //            gamePath.teleportsRemainingAt(0)
    //        )
    //        assertEquals(
    //            setOf(1, 2, 3),
    //            gamePath.teleportsRemainingAt(1)
    //        )
    //        assertEquals(
    //            setOf(2, 3),
    //            gamePath.teleportsRemainingAt(2)
    //        )
    //        assertEquals(
    //            setOf(3),
    //            gamePath.teleportsRemainingAt(3)
    //        )
    //        assertEquals(
    //            setOf(),
    //            gamePath.teleportsRemainingAt(4)
    //        )
    //        assertEquals(
    //            setOf(),
    //            gamePath.teleportsRemainingAt(5)
    //        )
    //    }

    @Test
    fun regressionTest1() {
        val gameMap = GameMap.create(
            10, 10,
            MapEntity.Start(3, 8),
            MapEntity.Finish(3, 0),
        )

        assertNotNull(PathFinder.getShortestPath(gameMap))
    }
}