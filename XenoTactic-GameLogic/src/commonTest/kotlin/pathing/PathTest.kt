package pathing



import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.pathing.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class PathTest {

    @Test
    fun getPathCutoffAtIntersection_twoPoints() {
        val path = Path.create(IPoint(0f, 0f), IPoint(3f, 3f))

        assertEquals(
            Path.create(IPoint(0f, 0f), IPoint(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(0, GameUnitTuple(2f, 2f)))
        )
    }

    @Test
    fun getPathCutoffAtIntersection_fivePoints() {
        val path = Path.create(
            IPoint(0f, 0f),
            IPoint(3f, 3f),
            IPoint(5f, 5f),
            IPoint(7f, 7f),
            IPoint(8f, 8f)
        )

        assertEquals(
            Path.create(IPoint(0f, 0f), IPoint(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(0, GameUnitTuple(2f, 2f)))
        )

        assertEquals(
            Path.create(IPoint(0f, 0f), IPoint(3f, 3f), IPoint(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(1, GameUnitTuple(2f, 2f)))
        )

        assertEquals(
            Path.create(IPoint(0f, 0f), IPoint(3f, 3f), IPoint(5f ,5f), IPoint(2f, 2f)),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(2, GameUnitTuple(2f, 2f)))
        )

        assertEquals(
            Path.create(
                IPoint(0f, 0f), IPoint(3f, 3f), IPoint(5f, 5f), IPoint(7f, 7f),
                IPoint(2f, 2f)
            ),
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(3, GameUnitTuple(2f, 2f)))
        )

        // Outside of segment idx
        assertFailsWith<IllegalArgumentException> {
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(4, GameUnitTuple(2f, 2f)))
        }
    }

    @Test
    fun getPathCutoffAtIntersection_onlyOnePoint_shouldFail() {
        val path = Path.create(IPoint(0f, 0f))

        assertFailsWith<IllegalArgumentException> {
            path.getPathCutoffAtIntersection(Path.CircleIntersectionResult(0, GameUnitTuple(2f, 2f)))
        }
    }
}