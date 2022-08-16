package pathing

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.*
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SearcherTest {

    val TEST_DIAGONAL_DIFF: Double = 0.1 / sqrt(2.0)

    @Test
    fun getShortestPath_onlyStartAndFinishGiven() {
        val path = BFSSearcher.getShortestPath(
            10, 10, listOf(MapEntity.Start(0, 0), MapEntity.Finish(2, 0)),
        )

        val expectedPath = PathSequence.create(Path(listOf(Point(1.0, 1.0), Point(3.0, 1.0))))

        assertEquals(expectedPath, path)
    }

    @Test
    fun getShortestPath() {
        val path = BFSSearcher.getShortestPath(
            10, 10,
            listOf(MapEntity.Start(0, 0), MapEntity.Checkpoint(0, 3, 0), MapEntity.Finish(6, 0)),
        )
        print(path)
    }

    @Test
    fun getPathingPointsForSquare() {
        assertEquals(
            setOf<PathingPoint>(
                // Top left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(1.0, 2.1, VerticalDirection.DOWN, HorizontalDirection.NONE),
                // Top right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 2.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
                // Bottom left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                // Bottom right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1
                )
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_hasBlockingTop() {
        assertEquals(
            setOf<PathingPoint>(
                // Bottom left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                // Bottom right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1
                ),
                blockingPoints = BlockingPointContainer.View.create(MapEntity.ROCK_1X1.at(1, 2))
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_hasBlockingBottom() {
        assertEquals(
            setOf<PathingPoint>(
                // Top left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(1.0, 2.1, VerticalDirection.DOWN, HorizontalDirection.NONE),
                // Top right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 2.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                )
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1
                ),
                blockingPoints = BlockingPointContainer.View.create(
                    MapEntity.ROCK_1X1.at(1, 0)
                )
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_onlyTopLeft() {
        assertEquals(
            setOf<PathingPoint>(
                // Top left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 2.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1,
                    calculateTopLeft = CornerPathingPointConfiguration.FULLY_ENABLED,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED
                )
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_onlyTopRight() {
        assertEquals(
            setOf<PathingPoint>(
                // Top right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 2.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = CornerPathingPointConfiguration.FULLY_ENABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED
                )
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_onlyBottomLeft() {
        assertEquals(
            setOf<PathingPoint>(
                // Bottom left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.FULLY_ENABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED
                )
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_onlyBottomRight() {
        assertEquals(
            setOf<PathingPoint>(
                // Bottom right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
            ),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.FULLY_ENABLED
                )
            )
        )
    }

    @Test
    fun getPathingPointsForSquare_dontCalculate() {
        assertEquals(
            setOf<PathingPoint>(),
            getPathingPointsForUnitSquare(
                PathingPointsForUnitSquareConfiguration(
                    1, 1, 3,
                    3, 0.1,
                    calculateTopLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateTopRight = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomLeft = CornerPathingPointConfiguration.DISABLED,
                    calculateBottomRight = CornerPathingPointConfiguration.DISABLED
                )
            )
        )
    }

    @Test
    fun getAvailablePathingPointsFromBlockingEntitiesTest() {
        assertEquals(
            setOf<PathingPoint>(
                // Top left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 2.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                // Top right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 2.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 2.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 2.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
                // Bottom left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                // Bottom right
                PathingPoint.create(
                    2.0 + TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    2.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    2.1, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
            ),
            getAvailablePathingPointsFromBlockingEntities(
                listOf(
                    MapEntity.Rock(IntPoint(1, 1), 1, 1)
                ), 3, 3,
                BlockingPointContainer.View.create(MapEntity.Rock(IntPoint(1, 1), 1, 1)),
                cornerPathingPointConfiguration = CornerPathingPointConfiguration.FULLY_ENABLED,
                pathingPointPrecision = 0.1
            )
        )
    }

    @Test
    fun getAvailablePathingPointsFromBlockingEntities2() {
        assertEquals(
            setOf<PathingPoint>(
                // Top left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 3f + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 3.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 3.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                // Top right
                PathingPoint.create(
                    5.0 + TEST_DIAGONAL_DIFF, 3.0 + TEST_DIAGONAL_DIFF,
                    VerticalDirection.DOWN, HorizontalDirection.LEFT
                ),
                PathingPoint.create(
                    5.0, 3.1,
                    VerticalDirection.DOWN, HorizontalDirection.NONE
                ),
                PathingPoint.create(
                    5.1, 3.0,
                    VerticalDirection.NONE, HorizontalDirection.LEFT
                ),
                // Bottom left
                PathingPoint.create(
                    1.0 - TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    0.9, 1.0,
                    VerticalDirection.NONE, HorizontalDirection.RIGHT
                ),
                PathingPoint.create(
                    1.0, 0.9,
                    VerticalDirection.UP, HorizontalDirection.NONE
                ),
                // Bottom right
                PathingPoint.create(
                    5f + TEST_DIAGONAL_DIFF, 1.0 - TEST_DIAGONAL_DIFF,
                    VerticalDirection.UP, HorizontalDirection.LEFT
                ),
                PathingPoint.create(5.0, 0.9, VerticalDirection.UP, HorizontalDirection.NONE),
                PathingPoint.create(5.1, 1.0, VerticalDirection.NONE, HorizontalDirection.LEFT),
            ),
            getAvailablePathingPointsFromBlockingEntities(
                listOf(
                    MapEntity.Rock(IntPoint(1, 1), 4, 2)
                ), 10, 10,
                BlockingPointContainer.View.create(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
                cornerPathingPointConfiguration = CornerPathingPointConfiguration.FULLY_ENABLED,
                pathingPointPrecision = 0.1
            )
        )
    }

    @Test
    fun lineIntersectsEntityTest() {
        assertTrue(
            lineIntersectsEntity(
                Point(1.0, 1.0), Point(2.0, 2.0),
                MapEntity.Rock(IntPoint(1, 1), 1, 1)
            )
        )

        assertTrue(
            lineIntersectsEntity(
                Point(1.0, 1.0), Point(2.0, 2.0),
                MapEntity.Rock(IntPoint(2, 2), 1, 1)
            )
        )
    }
}