package pathing

import com.soywiz.korio.lang.assert
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Segment
import com.xenotactic.gamelogic.test_utils.TEST_DOUBLE_MAX_DELTA
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.testing.assertThat
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


internal class SegmentTraversalTest {

    @Test
    fun traverse() {
        val segment = Segment(Point(1.0, 1.0), Point(3.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)
        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            1.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            1.0,
            TEST_DOUBLE_MAX_DELTA
        )

        segmentTraversal.traverse(2.0)

        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            1.0 + sqrt(2.0),
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            1.0 + sqrt(2.0),
            TEST_DOUBLE_MAX_DELTA
        )
        println(segmentTraversal.currentPosition)
    }

    @Test
    fun traverse2() {
        val segment = Segment(Point(0.0, 0.0), Point(0.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)

        segmentTraversal.traverse(1.0)

        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            1.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.distanceTraversed).almostEqualsTo(1.0)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(0.5.toGameUnit())
        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            1.5,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.distanceTraversed).almostEqualsTo(1.5)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(1.0)
        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(2.5)
        assertThat(segmentTraversal.distanceTraversed).almostEqualsTo(2.5)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(0.5)
        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            3.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assert(segmentTraversal.finishedTraversal())
        assertThat(segmentTraversal.distanceTraversed).almostEqualsTo(3.0)

        segmentTraversal.traverse(0.5)
        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            3.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.nonTraversedDistance).almostEqualsTo(0.0)
        assertThat(segmentTraversal.distanceTraversed).almostEqualsTo(3.0)
    }

    @Test
    fun traverse_traversesWholeSegmentInOneStep() {
        val segment = Segment(Point(0.0, 0.0), Point(0.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)

        segmentTraversal.traverse(10.0)
        assertThat(segmentTraversal.currentPosition.x).almostEqualsTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).almostEqualsTo(
            3.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.distanceTraversed).almostEqualsTo(3.0)
        assertThat(segmentTraversal.nonTraversedDistance).almostEqualsTo(7.0)
    }
}