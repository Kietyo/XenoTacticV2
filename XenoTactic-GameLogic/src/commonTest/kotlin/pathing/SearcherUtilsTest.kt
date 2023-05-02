package pathing



import com.kietyo.ktruth.assertThat
import com.xenotactic.gamelogic.utils.PATHING_RADIUS
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.*
import kotlin.test.Test

internal class SearcherUtilsTest {
    @Test
    fun getNextPointsTest() {
        val startCenter = IPoint(4f, 7f)

        val pathingPoint = PathingPoint(
            IPoint(6.007071f, 7.007071f),
            VerticalDirection.DOWN,
            HorizontalDirection.LEFT
        )

        val startCirclePointToPathingPoint =
            calculateShortestPointFromStartToEndCircle(pathingPoint.v, startCenter, PATHING_RADIUS)

        assertThat(getNextPoints(
            startCirclePointToPathingPoint,
            listOf(MapEntity.Rock(2, 5, 4, 2)),
            setOf(pathingPoint)
        )).containsExactlyUnordered(pathingPoint.v)

//        assertEquals(
//            listOf(
//                pathingPoint.v
//            ), getNextPoints(
//                startCirclePointToPathingPoint,
//                listOf(MapEntity.Rock(2, 5, 4, 2)),
//                setOf(pathingPoint)
//            )
//        )
    }
}