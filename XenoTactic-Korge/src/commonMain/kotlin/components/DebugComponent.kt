package components

import com.soywiz.korma.geom.Point
import engine.Component
import engine.Engine
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.pathing.PathingPoint
import com.xenotactic.gamelogic.pathing.SearcherType
import com.xenotactic.gamelogic.pathing.getAvailablePathingPointsFromBlockingEntities


sealed class DebugPathingPoints {
    data class ForEntity(
        val cursorPosition: IntPoint,
        val entity: MapEntity,
        val pathingPoints: List<Point> = listOf()
    ): DebugPathingPoints()

    object None : DebugPathingPoints()
}

class DebugComponent(val engine: Engine) : Component {
    val pathTypeToPaths = mutableMapOf<SearcherType, PathSequence?>()

    val pathingPoints = mutableListOf<PathingPoint>()
    var isPathingPointEnabled = false

    var pathingPointsForEntity: DebugPathingPoints = DebugPathingPoints.None

    fun updatePathingPoints() {
        if (!isPathingPointEnabled) {
            pathingPoints.clear()
            return
        }
        val mapComponent = engine.getOneTimeComponent<GameMapControllerComponent>()
        val gameMap = mapComponent.getGameMapDebugOnly()
        pathingPoints.clear()
        pathingPoints.addAll(
            getAvailablePathingPointsFromBlockingEntities(
                gameMap
                    .getBlockingEntities(),
                gameMap.width,
                gameMap.height,
                gameMap.blockingPointsView()
            )
        )
    }
}