package pathing

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.PathAttribute
import com.xenotactic.gamelogic.pathing.Segment
import test_utils.assertPointEquals
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SegmentTest {

    @Test
    fun getFirstIntersectionPoint_standard() {
        val segment = Segment(Point(0f, 0f), Point(8.0, 8.0))
        assertPointEquals(Point(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
            segment.getFirstIntersectionPointToCircle(Point(3.0, 3.0), 3.0)!!)
    }

    @Test
    fun getFirstIntersectionPoint_standard_reversed() {
        val segment = Segment(Point(8f, 8f), Point(0f, 0f))
        assertPointEquals(Point(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            segment.getFirstIntersectionPointToCircle(Point(3f, 3f), 3.0)!!)
    }

    @Test
    fun getFirstIntersectionPoint_onlyOnePointInSegmentsIntersects() {
        val segment = Segment(Point(0f, 0f), Point(3f, 3f))
        assertPointEquals(
            Point(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
            segment.getFirstIntersectionPointToCircle(Point(3f, 3f), 3.0)!!
        )
    }

    @Test
    fun getFirstIntersectionPoint_segmentDoesntIntersect() {
        val segment = Segment(Point(-3f, -3f), Point(0f, 0f))
        assertNull(segment.getFirstIntersectionPointToCircle(Point(3f, 3f), 3.0))
    }

    @Test
    fun effectiveLength_noAttributes() {
        val segment = Segment(Point(0f, 0f), Point(0f, 3f))
        assertEquals(3.0, segment.effectiveLength)
    }

    @Test
    fun effectiveLength_speedAttribute() {
        run {
            val segment = Segment(Point(0f, 0f), Point(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(0.5))
            assertEquals(6.0, segment.effectiveLength)
        }
        run {
            val segment = Segment(Point(0f, 0f), Point(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(0.5))
            segment.addAttribute(PathAttribute.SpeedEffect(0.5))
            assertEquals(12.0, segment.effectiveLength)
        }
        run {
            val segment = Segment(Point(0f, 0f), Point(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(2.0))
            assertEquals(1.5, segment.effectiveLength)
        }
        run {
            val segment = Segment(Point(0f, 0f), Point(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(2.0))
            segment.addAttribute(PathAttribute.SpeedEffect(2.0))
            assertEquals(0.75, segment.effectiveLength)
        }
    }
}