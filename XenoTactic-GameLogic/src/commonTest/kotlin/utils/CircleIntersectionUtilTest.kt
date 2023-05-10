package utils



import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.assertEquals
import com.xenotactic.gamelogic.utils.CircleIntersectionUtil
import kotlin.math.sqrt
import kotlin.test.Test

internal class CircleIntersectionUtilTest {

    @Test
    fun assertEquals_circleIntersectionResults() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
                IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            ),
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
                IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
                IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            ),
            CircleIntersectionUtil(
                IPoint(0f, 0f),
                IPoint(8f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3f, 0f),
                IPoint(3f, 6f),
            ),
            CircleIntersectionUtil(
                IPoint(3f, 0f),
                IPoint(3f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3f, 6f),
                IPoint(3f, 0f)
            ),
            CircleIntersectionUtil(
                IPoint(3f, 8f),
                IPoint(3f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(0f, 3f)),
            CircleIntersectionUtil(
                IPoint(0f, 0f),
                IPoint(0f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_onlyOnePartOfSegmentIntersects() {
        assertEquals(
            CircleIntersectionUtil.Result.PartialIntersection(
                false,
                true,
                IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ),
            CircleIntersectionUtil(
                IPoint(0f, 0f),
                IPoint(3f, 3f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_segmentDoesNotIntersectWithCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(-3f, -3f),
                IPoint(0f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
                IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ),
            CircleIntersectionUtil(
                IPoint(8f, 8f),
                IPoint(0f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(4f - sqrt(56f / 16), 4f + sqrt(56f / 16)),
                IPoint(4f + sqrt(56f / 16), 4f - sqrt(56f / 16))
            ),
            CircleIntersectionUtil(
                IPoint(0f, 8f),
                IPoint(8f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(4f + sqrt(56f / 16), 4f - sqrt(56f / 16)),
                IPoint(4f - sqrt(56f / 16), 4f + sqrt(56f / 16))
            ),
            CircleIntersectionUtil(
                IPoint(8f, 0f),
                IPoint(0f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_outsideOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(0f, 6f),
                IPoint(8f, 14f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(
                IPoint(0f, 3f)
            ),
            CircleIntersectionUtil(
                IPoint(0f, 8f),
                IPoint(0f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(6f, 3f)),
            CircleIntersectionUtil(
                IPoint(6f, 0f),
                IPoint(6f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(6f, 3f)),
            CircleIntersectionUtil(
                IPoint(6f, 8f),
                IPoint(6f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(true, false),
            CircleIntersectionUtil(
                IPoint(6f, 3f),
                IPoint(6f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle_reversed() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false,
                true
            ),
            CircleIntersectionUtil(
                IPoint(6f, 8f),
                IPoint(6f, 3f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false,
                true
            ),
            CircleIntersectionUtil(
                IPoint(6f, 0f),
                IPoint(6f, 3f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle_reversed() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                true,
                false
            ),
            CircleIntersectionUtil(
                IPoint(6f, 3f),
                IPoint(6f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(-1f, 0f),
                IPoint(-1f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(-1f, 8f),
                IPoint(-1f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(7f, 0f),
                IPoint(7f, 8f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(7f, 8f),
                IPoint(7f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(IPoint(0f, 3f), IPoint(6f, 3f)),
            CircleIntersectionUtil(
                IPoint(0f, 3f),
                IPoint(8f, 3f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(6f, 3f),
                IPoint(0f, 3f),
            ),
            CircleIntersectionUtil(
                IPoint(8f, 3f),
                IPoint(0f, 3f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(3f, 6f)),
            CircleIntersectionUtil(
                IPoint(0f, 6f),
                IPoint(8f, 6f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(3f, 6f)),
            CircleIntersectionUtil(
                IPoint(8f, 6f),
                IPoint(0f, 6f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(3f, 0f)),
            CircleIntersectionUtil(
                IPoint(0f, 0f),
                IPoint(8f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.Tangent(IPoint(3f, 0f)),
            CircleIntersectionUtil(
                IPoint(8f, 0f),
                IPoint(0f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false,
                true
            ),
            CircleIntersectionUtil(
                IPoint(0f, 0f),
                IPoint(3f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle_reversed() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                true,
                false
            ),
            CircleIntersectionUtil(
                IPoint(3f, 0f),
                IPoint(0f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                true, false
            ),
            CircleIntersectionUtil(
                IPoint(3f, 0f),
                IPoint(6f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle_reversed() {
        assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false, true
            ),
            CircleIntersectionUtil(
                IPoint(6f, 0f),
                IPoint(3f, 0f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(0f, 7f),
                IPoint(8f, 7f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(8f, 7f),
                IPoint(0f, 7f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(0f, -1f),
                IPoint(8f, -1f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom_reversedPoints() {
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(8f, -1f),
                IPoint(0f, -1f),
                IPoint(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1() {
        val tpIn = MapEntity.TeleportIn(0, GameUnitTuple(3, 3))
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3.9999115f, 5.0f),
                IPoint(3.9999115f, 3.0f),
            ),
            CircleIntersectionUtil(
                IPoint(3.9999115f, 8.95f),
                IPoint(3.9999995f, 1.05f),
                tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1_reversed() {
        val tpIn = MapEntity.TeleportIn(0, GameUnitTuple(3, 3))
        assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                IPoint(3.9999995f, 3.0f),
                IPoint(3.9999995f, 5.0f),
            ),
            CircleIntersectionUtil(
                IPoint(3.9999995f, 1.05f),
                IPoint(3.9999115f, 8.95f), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest2() {
        val tpIn = MapEntity.TeleportIn(0, GameUnitTuple(3, 3))
        assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                IPoint(2.992929f, 5.007071f),
                IPoint(3.0f, 3.99f),
                tpIn.centerPoint, tpIn.radius
            )
        )
    }
}