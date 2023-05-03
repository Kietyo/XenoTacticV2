package pathing

import korlibs.io.lang.assert
import com.xenotactic.gamelogic.model.IPoint


import com.xenotactic.gamelogic.pathing.Segment
import com.xenotactic.gamelogic.pathing.SegmentTraversal
import com.xenotactic.gamelogic.utils.TEST_DOUBLE_MAX_DELTA
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.testing.assertThat
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertFalse


internal class SegmentTraversalTest {

    @Test
    fun traverse() {
        val segment = Segment(IPoint(1.0, 1.0), IPoint(3.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)
        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            1.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            1.0,
            TEST_DOUBLE_MAX_DELTA
        )

        segmentTraversal.traverse(2.0)

        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            1.0 + sqrt(2.0),
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            1.0 + sqrt(2.0),
            TEST_DOUBLE_MAX_DELTA
        )
        println(segmentTraversal.currentPosition)
    }

    @Test
    fun traverse2() {
        val segment = Segment(IPoint(0.0, 0.0), IPoint(0.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)

        segmentTraversal.traverse(1.0)

        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            1.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.distanceTraversed).isEqualTo(1.0)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(0.5.toGameUnit())
        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            1.5,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.distanceTraversed).isEqualTo(1.5)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(1.0)
        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(2.5)
        assertThat(segmentTraversal.distanceTraversed).isEqualTo(2.5)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(0.5)
        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            3.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assert(segmentTraversal.finishedTraversal())
        assertThat(segmentTraversal.distanceTraversed).isEqualTo(3.0)

        segmentTraversal.traverse(0.5)
        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            3.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.nonTraversedDistance).isEqualTo(0.0)
        assertThat(segmentTraversal.distanceTraversed).isEqualTo(3.0)
    }

    @Test
    fun traverse_traversesWholeSegmentInOneStep() {
        val segment = Segment(IPoint(0.0, 0.0), IPoint(0.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)

        segmentTraversal.traverse(10.0)
        assertThat(segmentTraversal.currentPosition.x).isEqualTo(
            0.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.currentPosition.y).isEqualTo(
            3.0,
            TEST_DOUBLE_MAX_DELTA
        )
        assertThat(segmentTraversal.distanceTraversed).isEqualTo(3.0)
        assertThat(segmentTraversal.nonTraversedDistance).isEqualTo(7.0)
    }
}