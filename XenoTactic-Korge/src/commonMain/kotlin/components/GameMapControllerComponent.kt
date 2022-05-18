package components

import com.soywiz.klogger.Logger
import com.xenotactic.gamelogic.globals.GAME_HEIGHT
import com.xenotactic.gamelogic.globals.GAME_WIDTH
import com.xenotactic.gamelogic.model.GameMap
import engine.Component
import engine.Engine
import events.AddEntityEvent
import events.EventBus
import events.RemovedEntityEvent
import events.UpdatedPathLengthEvent
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder


class GameMapControllerComponent(
    val engine: Engine, val eventBus: EventBus,
    private var gameMap: GameMap = GameMap(GAME_WIDTH, GAME_HEIGHT)
) : Component {

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
        gameMap.placeEntity(entity)
        updateShortestPath(PathFinder.getShortestPath(gameMap))
        eventBus.send(AddEntityEvent(entity))
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

        engine.getOneTimeComponentNullable<DebugComponent>()?.updatePathingPoints()

        eventBus.send(UpdatedPathLengthEvent(shortestPath?.pathLength))
    }

    fun getGameMapDebugOnly(): GameMap {
        return gameMap
    }

    companion object {
        val log = Logger<GameMapControllerComponent>()
    }
}