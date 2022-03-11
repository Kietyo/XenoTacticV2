package utils

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.CircleIntersectionUtil
import kotlin.math.sqrt
import kotlin.test.Test

internal class CircleIntersectionUtilTest {

    @Test
    fun assertEquals_circleIntersectionResults() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
                Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            ),
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
                Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
                Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            ),
            CircleIntersectionUtil(
                Point(0f, 0f),
                Point(8f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3f, 0f),
                Point(3f, 6f),
            ),
            CircleIntersectionUtil(
                Point(3f, 0f),
                Point(3f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_atMiddleOfCircle_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3f, 6f),
                Point(3f, 0f)
            ),
            CircleIntersectionUtil(
                Point(3f, 8f),
                Point(3f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(0f, 3f)),
            CircleIntersectionUtil(
                Point(0f, 0f),
                Point(0f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_onlyOnePartOfSegmentIntersects() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.PartialIntersection(
                false,
                true,
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ),
            CircleIntersectionUtil(
                Point(0f, 0f),
                Point(3f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_segmentDoesNotIntersectWithCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(-3f, -3f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
                Point(3 - sqrt(4.5f), 3 - sqrt(4.5f))
            ),
            CircleIntersectionUtil(
                Point(8f, 8f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(4f - sqrt(56f / 16), 4f + sqrt(56f / 16)),
                Point(4f + sqrt(56f / 16), 4f - sqrt(56f / 16))
            ),
            CircleIntersectionUtil(
                Point(0f, 8f),
                Point(8f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_negativeSlope_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(4f + sqrt(56f / 16), 4f - sqrt(56f / 16)),
                Point(4f - sqrt(56f / 16), 4f + sqrt(56f / 16))
            ),
            CircleIntersectionUtil(
                Point(8f, 0f),
                Point(0f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_standardLine_outsideOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(0f, 6f),
                Point(8f, 14f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentLeftPointOfCircle_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(
                Point(0f, 3f)
            ),
            CircleIntersectionUtil(
                Point(0f, 8f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(6f, 3f)),
            CircleIntersectionUtil(
                Point(6f, 0f),
                Point(6f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_tangentRightPointOfCircle_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(6f, 3f)),
            CircleIntersectionUtil(
                Point(6f, 8f),
                Point(6f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(true, false),
            CircleIntersectionUtil(
                Point(6f, 3f),
                Point(6f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_bottomTipTouchesTangentOfCircle_reversed() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false,
                true
            ),
            CircleIntersectionUtil(
                Point(6f, 8f),
                Point(6f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false,
                true
            ),
            CircleIntersectionUtil(
                Point(6f, 0f),
                Point(6f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_topTipTouchesTangentOfCircle_reversed() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                true,
                false
            ),
            CircleIntersectionUtil(
                Point(6f, 3f),
                Point(6f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(-1f, 0f),
                Point(-1f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleLeft_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(-1f, 8f),
                Point(-1f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(7f, 0f),
                Point(7f, 8f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_verticalLine_outsideOfCircleRight_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(7f, 8f),
                Point(7f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(Point(0f, 3f), Point(6f, 3f)),
            CircleIntersectionUtil(
                Point(0f, 3f),
                Point(8f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_atMiddleOfCircle_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(6f, 3f),
                Point(0f, 3f),
            ),
            CircleIntersectionUtil(
                Point(8f, 3f),
                Point(0f, 3f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(3f, 6f)),
            CircleIntersectionUtil(
                Point(0f, 6f),
                Point(8f, 6f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentTopPointOfCircle_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(3f, 6f)),
            CircleIntersectionUtil(
                Point(8f, 6f),
                Point(0f, 6f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(3f, 0f)),
            CircleIntersectionUtil(
                Point(0f, 0f),
                Point(8f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_tangentBottomPointOfCircle_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Tangent(Point(3f, 0f)),
            CircleIntersectionUtil(
                Point(8f, 0f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false,
                true
            ),
            CircleIntersectionUtil(
                Point(0f, 0f),
                Point(3f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_rightTipOfSegmentTouchesBottomOfCircle_reversed() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                true,
                false
            ),
            CircleIntersectionUtil(
                Point(3f, 0f),
                Point(0f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                true, false
            ),
            CircleIntersectionUtil(
                Point(3f, 0f),
                Point(6f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_leftTipOfSegmentTouchesBottomOfCircle_reversed() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.Touching(
                false, true
            ),
            CircleIntersectionUtil(
                Point(6f, 0f),
                Point(3f, 0f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(0f, 7f),
                Point(8f, 7f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleTop_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(8f, 7f),
                Point(0f, 7f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(0f, -1f),
                Point(8f, -1f),
                Point(3f, 3f),
                3.0
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_horizontalLine_outsideOfCircleBottom_reversedPoints() {
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
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
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3.9999115f, 5.0f),
                Point(3.9999115f, 3.0f),
            ),
            CircleIntersectionUtil(
                Point(3.9999115f, 8.95f),
                Point(3.9999995f, 1.05f),
                tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest1_reversed() {
        val tpIn = MapEntity.TeleportIn(0, IntPoint(3, 3))
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.FullIntersection(
                Point(3.9999995f, 3.0f),
                Point(3.9999995f, 5.0f),
            ),
            CircleIntersectionUtil(
                Point(3.9999995f, 1.05f),
                Point(3.9999115f, 8.95f), tpIn.centerPoint, tpIn.radius
            )
        )
    }

    @Test
    fun getIntersectionPointsOfLineSegmentAndCircle_regressionTest2() {
        val tpIn = MapEntity.TeleportIn(0, IntPoint(3, 3))
        test_utils.assertEquals(
            CircleIntersectionUtil.Result.NoIntersection,
            CircleIntersectionUtil(
                Point(2.992929f, 5.007071f),
                Point(3.0f, 3.99f),
                tpIn.centerPoint, tpIn.radius
            )
        )
    }
}