package pathing



import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.pathing.PathAttribute
import com.xenotactic.gamelogic.pathing.Segment
import com.xenotactic.gamelogic.utils.assertPointEquals
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.testing.assertThat
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertNull

internal class SegmentTest {
    @Test
    fun getFirstIntersectionPoint_standard() {
        val segment = Segment(IPoint(0f, 0f), IPoint(8.0, 8.0))
        assertPointEquals(IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
            segment.getFirstIntersectionPointToCircle(IPoint(3.0, 3.0), 3.0)!!)
    }

    @Test
    fun getFirstIntersectionPoint_standard_reversed() {
        val segment = Segment(IPoint(8f, 8f), IPoint(0f, 0f))
        assertPointEquals(IPoint(3 + sqrt(4.5f), 3 + sqrt(4.5f)),
            segment.getFirstIntersectionPointToCircle(IPoint(3f, 3f), 3.0)!!)
    }

    @Test
    fun getFirstIntersectionPoint_onlyOnePointInSegmentsIntersects() {
        val segment = Segment(IPoint(0f, 0f), IPoint(3f, 3f))
        assertPointEquals(
            IPoint(3 - sqrt(4.5f), 3 - sqrt(4.5f)),
            segment.getFirstIntersectionPointToCircle(IPoint(3f, 3f), 3.0)!!
        )
    }

    @Test
    fun getFirstIntersectionPoint_segmentDoesntIntersect() {
        val segment = Segment(IPoint(-3f, -3f), IPoint(0f, 0f))
        assertNull(segment.getFirstIntersectionPointToCircle(IPoint(3f, 3f), 3.0))
    }

    @Test
    fun effectiveLength_noAttributes() {
        val segment = Segment(IPoint(0f, 0f), IPoint(0f, 3f))
        assertEquals(3.0, segment.effectiveLength)
    }

    @Test
    fun effectiveLength_speedAttribute() {
        run {
            val segment = Segment(IPoint(0f, 0f), IPoint(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(0.5))
            assertEquals(6.0, segment.effectiveLength)
        }
        run {
            val segment = Segment(IPoint(0f, 0f), IPoint(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(0.5))
            segment.addAttribute(PathAttribute.SpeedEffect(0.5))
            assertEquals(12.0, segment.effectiveLength)
        }
        run {
            val segment = Segment(IPoint(0f, 0f), IPoint(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(2.0))
            assertEquals(1.5, segment.effectiveLength)
        }
        run {
            val segment = Segment(IPoint(0f, 0f), IPoint(0f, 3f))
            segment.addAttribute(PathAttribute.SpeedEffect(2.0))
            segment.addAttribute(PathAttribute.SpeedEffect(2.0))
            assertEquals(0.75, segment.effectiveLength)
        }
    }

    fun assertEquals(expected: Double, actual: GameUnit) {
        assertThat(actual).isEqualTo(expected)
    }
}