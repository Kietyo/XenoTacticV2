package components

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.globals.GAME_HEIGHT
import com.xenotactic.gamelogic.globals.GAME_WIDTH
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.GameMap
import engine.EComponent
import engine.Engine
import events.AddEntityEvent
import events.EventBus
import events.RemovedEntityEvent
import events.UpdatedPathLengthEvent
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.rectangleIntersects
import pathing.PathFinder
import kotlin.math.max
import kotlin.math.min


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

    fun getFirstRockAt(x: Int, y: Int): MapEntity.Rock? {
        return gameMap.getFirstRockAt(x, y)
    }

    fun getFirstTowerAt(x: Int, y: Int): MapEntity.Tower? {
        return gameMap.getFirstTowerAt(x, y)
    }

    fun intersectsBlockingEntities(entity: MapEntity): Boolean {
        return gameMap.intersectsBlockingEntities(entity)
    }

    fun placeEntity(entity: MapEntity) {
        if (!rectangleIntersects(
                GRectInt(0, 0, gameMap.width, gameMap.height),
                entity.getGRectInt()
            )
        ) {
            return
        }

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
        updateShortestPath(PathFinder.getShortestPath(gameMap))
        eventBus.send(AddEntityEvent(placementEntity))
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

    companion object {
        val log = Logger<GameMapControllerEComponent>()
    }
}