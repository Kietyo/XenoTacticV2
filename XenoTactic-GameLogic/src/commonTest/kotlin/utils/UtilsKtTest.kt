package utils

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.RectangleInt
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.getIntersectionPointsOfLineSegmentAndCircle
import com.xenotactic.gamelogic.utils.getIntersectionPointsOfLineSegmentAndRectangle
import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.gamelogic.utils.rectangleIntersects
import test_utils.assertPointSetEquals
import test_utils.randomVector
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class UtilsKtTest {

    data class IntersectionPointArgs(
        val p1: Point,
        val p2: Point,
        val circleCenter: Point,
        val radius: Double
    ) {
        constructor(
            p1: Point,
            p2: Point,
            circleCenter: Point,
            radius: Float
        ) : this(p1, p2, circleCenter, radius.toDouble())
    }

    @Test
    @Ignore
    fun fuzzTest() {
        var totalTime = 0L
        var min = Long.MAX_VALUE
        var max = Long.MIN_VALUE
        var count = 0
        val expensiveArgs = mutableListOf<IntersectionPointArgs>()
        repeat(1000000) {
            count++
            val args = IntersectionPointArgs(
                randomVector(),
                randomVector(),
                randomVector(),
                Random.nextFloat()
            )
            val time = measureTime {
                getIntersectionPointsOfLineSegmentAndCircle(
                    args.p1,
                    args.p2,
                    args.circleCenter,
                    args.radius
                )
            }.first
            totalTime += time
            min = min(time, min)
            if (time >= max) {
                max = time
                expensiveArgs.add(args)
            }
        }
        println("totalTime: $totalTime, min: $min, max: $max, avg: ${totalTime / count}")
        println("expensiveArgs: $expensiveArgs")
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine() {
        assertPointSetEquals(
            setOf(
                Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 0f),
                Point(8f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_onlyOnePartOfSegmentIntersects() {
        assertPointSetEquals(
            setOf(Point(3 - sqrt(4.5f), 3 - sqrt(4.5f))),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 0f),
                Point(3f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_segmentDoesNotIntersectWithCircle() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(-3f, -3f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_reversedPoints() {
        assertPointSetEquals(
            setOf(
                Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, 8f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope() {
        assertPointSetEquals(
            setOf(
                Point(4f + sqrt(56f / 16), 4f - sqrt(56f / 16)),
                Point(4f - sqrt(56f / 16), 4f + sqrt(56f / 16))
            ),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 8f),
                Point(8f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope_reversedPoints() {
        assertPointSetEquals(
            setOf(
                Point(4f + sqrt(56f / 16), 4f - sqrt(56f / 16)),
                Point(4f - sqrt(56f / 16), 4f + sqrt(56f / 16))
            ),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, 0f),
                Point(0f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_outsideOfCircle() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 6f),
                Point(8f, 14f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle() {
        assertEquals(
            setOf(Point(3f, 6f), Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(3f, 0f),
                Point(3f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle_reversedPoints() {
        assertEquals(
            setOf(Point(3f, 6f), Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(3f, 8f),
                Point(3f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle() {
        assertEquals(
            setOf(Point(0f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 0f),
                Point(0f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(Point(0f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 8f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle() {
        assertEquals(
            setOf(Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 0f),
                Point(6f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 8f),
                Point(6f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle() {
        assertEquals(
            setOf(Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 3f),
                Point(6f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle_reversed() {
        assertEquals(
            setOf(Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 8f),
                Point(6f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle() {
        assertEquals(
            setOf(Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 0f),
                Point(6f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle_reversed() {
        assertEquals(
            setOf(Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 3f),
                Point(6f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(-1f, 0f),
                Point(-1f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft_reversedPoints() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(-1f, 8f),
                Point(-1f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(7f, 0f),
                Point(7f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight_reversedPoints() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(7f, 8f),
                Point(7f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle() {
        assertEquals(
            setOf(Point(0f, 3f), Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 3f),
                Point(8f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle_reversedPoints() {
        assertEquals(
            setOf(Point(0f, 3f), Point(6f, 3f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, 3f),
                Point(0f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle() {
        assertEquals(
            setOf(Point(3f, 6f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 6f),
                Point(8f, 6f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(Point(3f, 6f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, 6f),
                Point(0f, 6f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle() {
        assertEquals(
            setOf(Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 0f),
                Point(8f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, 0f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle() {
        assertEquals(
            setOf(Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 0f),
                Point(3f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle_reversed() {
        assertEquals(
            setOf(Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(3f, 0f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle() {
        assertEquals(
            setOf(Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(3f, 0f),
                Point(6f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle_reversed() {
        assertEquals(
            setOf(Point(3f, 0f)),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(6f, 0f),
                Point(3f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, 7f),
                Point(8f, 7f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop_reversedPoints() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, 7f),
                Point(0f, 7f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(0f, -1f),
                Point(8f, -1f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom_reversedPoints() {
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(8f, -1f),
                Point(0f, -1f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1() {
        val tpIn = MapEntity.TeleportIn(0, IntPoint(3, 3))
        assertPointSetEquals(
            setOf(
                Point(3.9999115f, 3.0f),
                Point(3.9999115f, 5.0f)
            ),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(3.9999115f, 8.95f), Point(
                    3.9999995f, 1.05f
                ), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1_reversed() {
        val tpIn = MapEntity.TeleportIn(0, IntPoint(3, 3))
        assertPointSetEquals(
            setOf(
                Point(3.9999995f, 3.0f),
                Point(3.9999995f, 5.0f)
            ),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(
                    3.9999995f, 1.05f
                ), Point(3.9999115f, 8.95f), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest2() {
        val tpIn = MapEntity.TeleportIn(0, IntPoint(3, 3))
        assertEquals(
            setOf<Point>(),
            getIntersectionPointsOfLineSegmentAndCircle(
                Point(2.992929f, 5.007071f), Point(
                    3.0f, 3.99f
                ), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_bottomAndTopSides() {
        assertEquals(
            setOf(
                Point(2f, 1f), Point(4f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(1f, -1f),
                Point(5f, 7f),
                Point(1f, 1f),
                5f, 4f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_bottomAndTopSides_reversed() {
        assertEquals(
            setOf(
                Point(4f, 5f), Point(2f, 1f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(5f, 7f),
                Point(1f, -1f),
                Point(1f, 1f),
                5f, 4f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftAndRightSides() {
        assertEquals(
            setOf(
                Point(2f, 1f), Point(4f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(1f, -1f),
                Point(5f, 7f),
                Point(2f, 0f),
                2f, 8f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftAndRightSides_reversed() {
        assertEquals(
            setOf(
                Point(2f, 1f), Point(4f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(5f, 7f),
                Point(1f, -1f),
                Point(2f, 0f),
                2f, 8f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftSideAndTopRightCorner() {
        assertEquals(
            setOf(
                Point(2f, 1f), Point(4f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(5f, 7f),
                Point(1f, -1f),
                Point(2f, 0f),
                2f, 5f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftSideAndTopSide() {
        assertEquals(
            setOf(
                Point(2f, 1f), Point(4f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(5f, 7f),
                Point(1f, -1f),
                Point(2f, 0f),
                4f, 5f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_verticalLine() {
        assertEquals(
            setOf(
                Point(3f, 1f), Point(3f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(3f, 0f),
                Point(3f, 6f),
                Point(1f, 1f),
                5f, 4f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest1() {
        assertEquals(
            setOf(
                Point(4f, 3f), Point(4f, 5f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(4f, 8.95f),
                Point(4f, 1f),
                Point(3f, 3f),
                2f, 2f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest2() {
        assertPointSetEquals(
            setOf(
                Point(4.992978f, 5.0f)
            ),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(5.0f, 6.01f),
                Point(4.992929f, 4.992929f),
                Point(3f, 3f),
                2f, 2f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest3() {
        assertEquals(
            emptySet<Point>(),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(6.01f, 8.0f),
                Point(6.0f, 6.99f),
                Point(3f, 3f),
                2f, 2f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest4() {
        assertEquals(
            emptySet<Point>(),
            getIntersectionPointsOfLineSegmentAndRectangle(
                Point(6.0f, 6.99f),
                Point(5.0f, 6.01f),
                Point(3f, 3f),
                2f, 2f
            )
        )
    }

    fun RectangleInt.testString(): String {
        return "left: ${this.left}, right: ${this.right}, top: ${this.top}, bottom: ${this.bottom}"
    }

    @Test
    fun rectangleIntersectsTest() {
        val a = GRectInt(0, 0, 3, 3)
        val b = GRectInt(2, 2, 3, 3)

        assertTrue(rectangleIntersects(
            GRectInt(0, 0, 3, 3),
            GRectInt(2, 2, 3, 3),
        ))
        assertTrue(rectangleIntersects(
            GRectInt(0, 0, 3, 3),
            GRectInt(1, 1, 3, 3),
        ))
        assertTrue(rectangleIntersects(
            GRectInt(0, 0, 3, 3),
            GRectInt(0, 0, 3, 3),
        ))

        assertTrue(rectangleIntersects(
            GRectInt(2, 2, 3, 3),
            GRectInt(0, 0, 3, 3),
        ))

        assertFalse(rectangleIntersects(
            GRectInt(0, 0, 3, 3),
            GRectInt(3, 3, 3, 3),
        ))
        assertFalse(rectangleIntersects(
            GRectInt(0, 0, 3, 3),
            GRectInt(3, 0, 3, 3),
        ))
        assertFalse(rectangleIntersects(
            GRectInt(0, 0, 3, 3),
            GRectInt(0, 3, 3, 3),
        ))
    }
}