package com.xenotactic.korge.ecomponents

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.globals.GAME_HEIGHT
import com.xenotactic.gamelogic.globals.GAME_WIDTH
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.rectangleIntersects
import com.xenotactic.korge.engine.EComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.RemovedEntityEvent
import com.xenotactic.korge.events.UpdatedPathLengthEvent
import pathing.PathFinder
import kotlin.math.max
import kotlin.math.min

// The game map controller.
// All actions affecting the game map should go through this class.
class GameMapControllerEComponent(
    val engine: Engine, val eventBus: EventBus,
    private var gameMap: GameMap = GameMap(GAME_WIDTH, GAME_HEIGHT)
) : EComponent {
    var shortestPath: PathSequence? = null
        private set

    val height: Int
        get() {
            return gameMap.height
        }
    val width: Int
        get() {
            return gameMap.width
        }
    val numCheckpoints: Int
        get() = gameMap.numCheckpoints

    val numTeleports: Int
        get() = gameMap.numTeleports

    fun getFirstRockAt(x: Int, y: Int): MapEntity.Rock? {
        return gameMap.getFirstRockAt(x, y)
    }

    fun getFirstTowerAt(x: Int, y: Int): MapEntity.Tower? {
        return gameMap.getFirstTowerAt(x, y)
    }

    fun intersectsBlockingEntities(entity: MapEntity): Boolean {
        return gameMap.intersectsBlockingEntities(entity)
    }

    fun placeEntities(vararg entities: MapEntity) {
        val gameMapRect = GRectInt(0, 0, gameMap.width, gameMap.height)
        val allEntitiesIntersectMap = entities.all {
            rectangleIntersects(gameMapRect, it.getGRectInt())
        }
        if (!allEntitiesIntersectMap) return

        for (entity in entities) {
            val placementEntity = when (entity) {
                is MapEntity.Rock -> {
                    val newX = max(entity.x, 0)
                    val newY = max(entity.y, 0)
                    val entityWidth = entity.width - (newX - entity.x)
                    val entityHeight = entity.height - (newY - entity.y)
                    val newWidth = min(gameMap.width, entity.x + entityWidth) - entity.x
                    val newHeight = min(gameMap.height, entity.y + entityHeight) - entity.y
                    MapEntity.Rock(newX, newY, newWidth, newHeight)
                }
                else -> entity
            }
            gameMap.placeEntity(placementEntity)
            eventBus.send(AddEntityEvent(placementEntity))
        }
        println("new gamemap: $gameMap")
        updateShortestPath(PathFinder.getShortestPath(gameMap))
    }

    fun placeEntity(entity: MapEntity) {
        placeEntities(entity)
    }

    fun removeEntity(entity: MapEntity) {
        gameMap.removeEntity(entity)
        updateShortestPath(PathFinder.getShortestPath(gameMap))
        eventBus.send(RemovedEntityEvent(entity))
    }

    fun updateMap(map: GameMap) {
        gameMap = map

        //        gameMap.placeEntities(
        //            MapEntity.Tower(13, 11),
        //            MapEntity.Tower(18, 5),
        //            MapEntity.Tower(8, 2),
        //        )
        updateShortestPath(PathFinder.getShortestPath(gameMap))

        //        val pathUpdater = PathUpdater(map)
        //        pathUpdater.placeEntities(
        //            MapEntity.Tower(13, 11),
        //            MapEntity.Tower(18, 5),
        //            MapEntity.Tower(8, 2),
        //        )
        //        updateShortestPath(pathUpdater.gamePath?.toPathSequence())
    }

    fun updateShortestPath(path: PathSequence?) {
        shortestPath = path

        engine.getOneTimeComponentNullable<DebugEComponent>()?.updatePathingPoints()

        eventBus.send(UpdatedPathLengthEvent(shortestPath?.pathLength))
    }

    fun getGameMapDebugOnly(): GameMap {
        return gameMap
    }

    fun getNotificationText(entityType: MapEntityType): String {
        val entityName = when (entityType) {
            MapEntityType.START -> "Start"
            MapEntityType.FINISH -> "Finish"
            MapEntityType.CHECKPOINT -> "Checkpoint ${numCheckpoints + 1}"
            MapEntityType.ROCK -> "Rock"
            MapEntityType.TOWER -> TODO()
            MapEntityType.TELEPORT_IN -> "Teleport In ${numTeleports + 1}"
            MapEntityType.TELEPORT_OUT -> "Teleport Out ${numTeleports + 1}"
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
        }

        return "Placement Mode: $entityName"
    }

    companion object {
        val log = Logger<GameMapControllerEComponent>()
    }
}