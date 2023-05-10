package utils

import com.kietyo.ktruth.assertThat
import korlibs.math.geom.MRectangleInt
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.assertPointSetEquals
import com.xenotactic.gamelogic.utils.randomVector
import com.xenotactic.gamelogic.utils.*
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.*

internal class UtilsKtTest {

    data class IntersectionPointArgs(val p1: IPoint, val p2: IPoint, val circleCenter: IPoint, val radius: Double) {
        constructor(p1: IPoint, p2: IPoint, circleCenter: IPoint, radius: Float) : this(
            p1, p2, circleCenter, radius.toDouble()
        )
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
                randomVector(), randomVector(), randomVector(), Random.nextFloat()
            )
            val time = measureTime {
                getIntersectionPointsOfLineSegmentAndCircle(
                    args.p1, args.p2, args.circleCenter, args.radius
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
                IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)), IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 0f), IPoint(8f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_onlyOnePartOfSegmentIntersects() {
        assertPointSetEquals(
            setOf(IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f))), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 0f), IPoint(3f, 3f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_segmentDoesNotIntersectWithCircle() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(-3f, -3f), IPoint(0f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_reversedPoints() {
        assertPointSetEquals(
            setOf(
                IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)), IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, 8f), IPoint(0f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope() {
        assertPointSetEquals(
            setOf(
                IPoint(4f + sqrt(56f / 16), 4f - sqrt(56f / 16)), IPoint(4f - sqrt(56f / 16), 4f + sqrt(56f / 16))
            ), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 8f), IPoint(8f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope_reversedPoints() {
        assertPointSetEquals(
            setOf(
                IPoint(4f + sqrt(56f / 16), 4f - sqrt(56f / 16)), IPoint(4f - sqrt(56f / 16), 4f + sqrt(56f / 16))
            ), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, 0f), IPoint(0f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_outsideOfCircle() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 6f), IPoint(8f, 14f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle() {
        assertEquals(
            setOf(IPoint(3f, 6f), IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(3f, 0f), IPoint(3f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle_reversedPoints() {
        assertEquals(
            setOf(IPoint(3f, 6f), IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(3f, 8f), IPoint(3f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle() {
        assertEquals(
            setOf(IPoint(0f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 0f), IPoint(0f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(IPoint(0f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 8f), IPoint(0f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle() {
        assertEquals(
            setOf(IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 0f), IPoint(6f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 8f), IPoint(6f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle() {
        assertEquals(
            setOf(IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 3f), IPoint(6f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle_reversed() {
        assertEquals(
            setOf(IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 8f), IPoint(6f, 3f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle() {
        assertEquals(
            setOf(IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 0f), IPoint(6f, 3f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle_reversed() {
        assertEquals(
            setOf(IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 3f), IPoint(6f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(-1f, 0f), IPoint(-1f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft_reversedPoints() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(-1f, 8f), IPoint(-1f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(7f, 0f), IPoint(7f, 8f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight_reversedPoints() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(7f, 8f), IPoint(7f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle() {
        assertEquals(
            setOf(IPoint(0f, 3f), IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 3f), IPoint(8f, 3f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle_reversedPoints() {
        assertEquals(
            setOf(IPoint(0f, 3f), IPoint(6f, 3f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, 3f), IPoint(0f, 3f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle() {
        assertEquals(
            setOf(IPoint(3f, 6f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 6f), IPoint(8f, 6f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(IPoint(3f, 6f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, 6f), IPoint(0f, 6f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle() {
        assertEquals(
            setOf(IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 0f), IPoint(8f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle_reversedPoints() {
        assertEquals(
            setOf(IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, 0f), IPoint(0f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle() {
        assertEquals(
            setOf(IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 0f), IPoint(3f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle_reversed() {
        assertEquals(
            setOf(IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(3f, 0f), IPoint(0f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle() {
        assertEquals(
            setOf(IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(3f, 0f), IPoint(6f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle_reversed() {
        assertEquals(
            setOf(IPoint(3f, 0f)), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(6f, 0f), IPoint(3f, 0f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, 7f), IPoint(8f, 7f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop_reversedPoints() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, 7f), IPoint(0f, 7f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(0f, -1f), IPoint(8f, -1f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom_reversedPoints() {
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(8f, -1f), IPoint(0f, -1f), IPoint(3f, 3f), 3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1() {
        val tpIn = MapEntity.TeleportIn(0, GameUnitTuple(3, 3))
        assertPointSetEquals(
            setOf(
                IPoint(3.9999115f, 3.0f), IPoint(3.9999115f, 5.0f)
            ), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(3.9999115f, 8.95f), IPoint(
                    3.9999995f, 1.05f
                ), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1_reversed() {
        val tpIn = MapEntity.TeleportIn(0, GameUnitTuple(3, 3))
        assertPointSetEquals(
            setOf(
                IPoint(3.9999995f, 3.0f), IPoint(3.9999995f, 5.0f)
            ), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(
                    3.9999995f, 1.05f
                ), IPoint(3.9999115f, 8.95f), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest2() {
        val tpIn = MapEntity.TeleportIn(0, GameUnitTuple(3, 3))
        assertEquals(
            setOf<IPoint>(), getIntersectionPointsOfLineSegmentAndCircle(
                IPoint(2.992929f, 5.007071f), IPoint(
                    3.0f, 3.99f
                ), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_bottomAndTopSides() {
        assertEquals(
            setOf(
                IPoint(2f, 1f), IPoint(4f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(1f, -1f), IPoint(5f, 7f), IPoint(1f, 1f), 5f, 4f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_bottomAndTopSides_reversed() {
        assertEquals(
            setOf(
                IPoint(4f, 5f), IPoint(2f, 1f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(5f, 7f), IPoint(1f, -1f), IPoint(1f, 1f), 5f, 4f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftAndRightSides() {
        assertEquals(
            setOf(
                IPoint(2f, 1f), IPoint(4f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(1f, -1f), IPoint(5f, 7f), IPoint(2f, 0f), 2f, 8f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftAndRightSides_reversed() {
        assertEquals(
            setOf(
                IPoint(2f, 1f), IPoint(4f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(5f, 7f), IPoint(1f, -1f), IPoint(2f, 0f), 2f, 8f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftSideAndTopRightCorner() {
        assertEquals(
            setOf(
                IPoint(2f, 1f), IPoint(4f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(5f, 7f), IPoint(1f, -1f), IPoint(2f, 0f), 2f, 5f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_leftSideAndTopSide() {
        assertEquals(
            setOf(
                IPoint(2f, 1f), IPoint(4f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(5f, 7f), IPoint(1f, -1f), IPoint(2f, 0f), 4f, 5f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_verticalLine() {
        assertEquals(
            setOf(
                IPoint(3f, 1f), IPoint(3f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(3f, 0f), IPoint(3f, 6f), IPoint(1f, 1f), 5f, 4f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest1() {
        assertEquals(
            setOf(
                IPoint(4f, 3f), IPoint(4f, 5f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(4f, 8.95f), IPoint(4f, 1f), IPoint(3f, 3f), 2f, 2f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest2() {
        assertPointSetEquals(
            setOf(
                IPoint(4.992978f, 5.0f)
            ), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(5.0f, 6.01f), IPoint(4.992929f, 4.992929f), IPoint(3f, 3f), 2f, 2f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest3() {
        assertEquals(
            emptySet<IPoint>(), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(6.01f, 8.0f), IPoint(6.0f, 6.99f), IPoint(3f, 3f), 2f, 2f
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndRectangle_regressionTest4() {
        assertEquals(
            emptySet<IPoint>(), getIntersectionPointsOfLineSegmentAndRectangle(
                IPoint(6.0f, 6.99f), IPoint(5.0f, 6.01f), IPoint(3f, 3f), 2f, 2f
            )
        )
    }

    fun MRectangleInt.testString(): String {
        return "left: ${this.left}, right: ${this.right}, top: ${this.top}, bottom: ${this.bottom}"
    }

    @Test
    fun rectangleIntersectsTest() {
        val a = GRectInt(0, 0, 3, 3)
        val b = GRectInt(2, 2, 3, 3)

        assertTrue(rectangleIntersects(a, b))
        assertTrue(rectangleIntersects(a, GRectInt(1, 1, 3, 3)))
        assertTrue(rectangleIntersects(a, a))
        assertTrue(rectangleIntersects(b, GRectInt(0, 0, 3, 3)))
        assertFalse(rectangleIntersects(a, GRectInt(3, 3, 3, 3)))
        assertFalse(rectangleIntersects(a, GRectInt(3, 0, 3, 3)))
        assertFalse(rectangleIntersects(a, GRectInt(0, 3, 3, 3)))
    }

    @Test
    fun gameUnit_minus() {
        assertEquals(GameUnit(5) - GameUnit(2), GameUnit(3))
    }

    @Test
    fun calculateCostOfUpgrades() {
        assertThat(calculateCostOfUpgrades(0, 4, 5))
            .isEqualTo(30)
        assertThat(calculateCostOfUpgrades(1, 4, 5))
            .isEqualTo(35)
    }
}