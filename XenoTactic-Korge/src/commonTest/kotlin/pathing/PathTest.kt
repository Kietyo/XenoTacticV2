package pathing

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class PathTest {

    @Test
    fun getPathCutoffAtIntersection_twoPoints() {
        val path = Path.create(Point(0f, 0f), Point(3f, 3f))

        assertEquals(
            Path.create(Point(0f, 0f), Point(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(0, Point(2f, 2f)))
        )
    }

    @Test
    fun getPathCutoffAtIntersection_fivePoints() {
        val path = Path.create(
            Point(0f, 0f),
            Point(3f, 3f),
            Point(5f, 5f),
            Point(7f, 7f),
            Point(8f, 8f))

        assertEquals(
            Path.create(Point(0f, 0f), Point(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(0, Point(2f, 2f)))
        )

        assertEquals(
            Path.create(Point(0f, 0f), Point(3f, 3f), Point(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(1, Point(2f, 2f)))
        )

        assertEquals(
            Path.create(Point(0f, 0f), Point(3f, 3f), Point(5f ,5f), Point(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(2, Point(2f, 2f)))
        )

        assertEquals(
            Path.create(Point(0f, 0f), Point(3f, 3f), Point(5f ,5f), Point(7f ,7f),
                Point(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(3, Point(2f, 2f)))
        )

        // Outside of segment idx
        assertFailsWith<IllegalArgumentException> {
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(4, Point(2f, 2f)))
        }
    }

    @Test
    fun getPathCutoffAtIntersection_onlyOnePoint_shouldFail() {
        val path = Path.create(Point(0f, 0f))

        assertFailsWith<IllegalArgumentException> {
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(0, Point(2f, 2f)))
        }
    }
}