package com.xenotactic.korge.ecomponents

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.GameUnitPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.pathing.PathingPoint
import com.xenotactic.gamelogic.pathing.SearcherType
import com.xenotactic.gamelogic.pathing.getAvailablePathingPointsFromBlockingEntities
import com.xenotactic.korge.engine.EComponent
import com.xenotactic.korge.engine.Engine

sealed class DebugPathingPoints {
    data class ForEntity(
        val cursorPosition: GameUnitPoint,
        val entity: MapEntity,
        val pathingPoints: List<Point> = listOf()
    ): DebugPathingPoints()

    object None : DebugPathingPoints()
}

class DebugEComponent(val engine: Engine) : EComponent {
    val pathTypeToPaths = mutableMapOf<SearcherType, PathSequence?>()

    val pathingPoints = mutableListOf<PathingPoint>()
    var isPathingPointEnabled = false

    var pathingPointsForEntity: DebugPathingPoints = DebugPathingPoints.None

    fun updatePathingPoints() {
        if (!isPathingPointEnabled) {
            pathingPoints.clear()
            return
        }
        val mapComponent = engine.injections.getSingleton<GameMapControllerEComponent>()
        val gameMap = mapComponent.getGameMapDebugOnly()
        pathingPoints.clear()
        pathingPoints.addAll(
            getAvailablePathingPointsFromBlockingEntities(
                gameMap
                    .getBlockingEntities(),
                gameMap.width.toInt(),
                gameMap.height.toInt(),
                gameMap.blockingPointsView()
            )
        )
    }
}