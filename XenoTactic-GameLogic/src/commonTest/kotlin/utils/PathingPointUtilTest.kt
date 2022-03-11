package utils

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

const val TEST_PATHING_RADIUS = 0.2
val TEST_DIAGONAL_XY_DELTA = TEST_PATHING_RADIUS / sqrt(2.0)

internal class PathingPointUtilTest {

    val util = PathingPointUtil(TEST_PATHING_RADIUS)

    @Test
    fun pathingPointUtil_oneBlockCovered() {
        val centerPoint = Point(2, 2)

        assertEquals(
            setOf(
                centerPoint + Point(TEST_DIAGONAL_XY_DELTA, TEST_DIAGONAL_XY_DELTA)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(TEST_DIAGONAL_XY_DELTA, -TEST_DIAGONAL_XY_DELTA)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(-TEST_DIAGONAL_XY_DELTA, TEST_DIAGONAL_XY_DELTA)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(2, 1),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(-TEST_DIAGONAL_XY_DELTA, -TEST_DIAGONAL_XY_DELTA)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
    }

    @Test
    fun pathingPointUtil_twoBlocksCovered() {
        val centerPoint = Point(2, 2)

        assertEquals(
            setOf(
                centerPoint + Point(TEST_PATHING_RADIUS, 0.0)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                    MapEntity.ROCK_1X1.at(1, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(0.0, -TEST_PATHING_RADIUS)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 2),
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(-TEST_PATHING_RADIUS, 0.0)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(2, 1),
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(0.0, TEST_PATHING_RADIUS)
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                    MapEntity.ROCK_1X1.at(2, 1),
                ).blockingPointsView()
            )
        )

        assertEquals(
            setOf(
                centerPoint + Point(TEST_DIAGONAL_XY_DELTA, TEST_DIAGONAL_XY_DELTA),
                centerPoint + Point(-TEST_DIAGONAL_XY_DELTA, -TEST_DIAGONAL_XY_DELTA),
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 2),
                    MapEntity.ROCK_1X1.at(2, 1),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(-TEST_DIAGONAL_XY_DELTA, TEST_DIAGONAL_XY_DELTA),
                centerPoint + Point(TEST_DIAGONAL_XY_DELTA, -TEST_DIAGONAL_XY_DELTA),
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(2, 2),
                    MapEntity.ROCK_1X1.at(1, 1),
                ).blockingPointsView()
            )
        )
    }

    @Test
    fun pathingPointUtil_threeBlocksCovered() {
        val centerPoint = Point(2, 2)
        assertEquals(
            setOf(
                centerPoint + Point(TEST_DIAGONAL_XY_DELTA, -TEST_DIAGONAL_XY_DELTA),
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                    MapEntity.ROCK_1X1.at(1, 2),
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(-TEST_DIAGONAL_XY_DELTA, -TEST_DIAGONAL_XY_DELTA),
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 2),
                    MapEntity.ROCK_1X1.at(2, 1),
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(-TEST_DIAGONAL_XY_DELTA, TEST_DIAGONAL_XY_DELTA),
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                    MapEntity.ROCK_1X1.at(2, 1),
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
        assertEquals(
            setOf(
                centerPoint + Point(TEST_DIAGONAL_XY_DELTA, TEST_DIAGONAL_XY_DELTA),
            ),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                    MapEntity.ROCK_1X1.at(1, 2),
                    MapEntity.ROCK_1X1.at(2, 1),
                ).blockingPointsView()
            )
        )
    }

    @Test
    fun pathingPointUtil_fullyCovered() {
        assertEquals(
            emptySet(),
            util.calculate(
                MapEntity.CHECKPOINT.at(1, 1),
                GameMap.create(
                    5, 5,
                    MapEntity.ROCK_1X1.at(1, 1),
                    MapEntity.ROCK_1X1.at(1, 2),
                    MapEntity.ROCK_1X1.at(2, 1),
                    MapEntity.ROCK_1X1.at(2, 2),
                ).blockingPointsView()
            )
        )
    }
}