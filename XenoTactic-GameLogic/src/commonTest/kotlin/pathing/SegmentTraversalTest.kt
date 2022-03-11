package pathing

import com.soywiz.korio.lang.assert
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Segment
import test_utils.TEST_DOUBLE_MAX_DELTA
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


internal class SegmentTraversalTest {

    @Test
    fun traverse() {
        val segment = Segment(Point(1.0, 1.0), Point(3.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)
        assertEquals(1.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(1.0,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)

        segmentTraversal.traverse(2.0)

        assertEquals(1.0 + sqrt(2.0),
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(1.0 + sqrt(2.0),
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        println(segmentTraversal.currentPosition)
    }

    @Test
    fun traverse2() {
        val segment = Segment(Point(0.0, 0.0), Point(0.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)

        segmentTraversal.traverse(1.0)

        assertEquals(0.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(1.0,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(1.0, segmentTraversal.distanceTraversed)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(0.5)
        assertEquals(0.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(1.5,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(1.5, segmentTraversal.distanceTraversed)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(1.0)
        assertEquals(0.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(2.5,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(2.5, segmentTraversal.distanceTraversed)
        assertFalse { segmentTraversal.finishedTraversal() }

        segmentTraversal.traverse(0.5)
        assertEquals(0.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(3.0,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        assert(segmentTraversal.finishedTraversal())
        assertEquals(3.0, segmentTraversal.distanceTraversed)

        segmentTraversal.traverse(0.5)
        assertEquals(0.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(3.0,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(0.0, segmentTraversal.nonTraversedDistance)
        assertEquals(3.0, segmentTraversal.distanceTraversed)
    }

    @Test
    fun traverse_traversesWholeSegmentInOneStep() {
        val segment = Segment(Point(0.0, 0.0), Point(0.0, 3.0))
        val segmentTraversal = SegmentTraversal(segment)

        segmentTraversal.traverse(10.0)
        assertEquals(0.0,
            segmentTraversal.currentPosition.x,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(3.0,
            segmentTraversal.currentPosition.y,
            TEST_DOUBLE_MAX_DELTA)
        assertEquals(3.0, segmentTraversal.distanceTraversed)
        assertEquals(7.0, segmentTraversal.nonTraversedDistance)
    }
}