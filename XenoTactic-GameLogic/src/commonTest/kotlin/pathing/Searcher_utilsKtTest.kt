package pathing


import com.soywiz.korma.geom.IPoint
import com.xenotactic.gamelogic.globals.PATHING_RADIUS
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Searcher_utilsKtTest {
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

        assertEquals(
            listOf(
                pathingPoint.v
            ), getNextPoints(
                startCirclePointToPathingPoint,
                listOf(MapEntity.Rock(2, 5, 4, 2)),
                setOf(pathingPoint)
            )
        )
    }
}