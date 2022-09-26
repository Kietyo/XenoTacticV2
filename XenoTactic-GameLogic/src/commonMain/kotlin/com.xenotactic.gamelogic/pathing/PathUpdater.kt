package pathing

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.SearcherInterface

class PathUpdater(
    val gameMap: GameMap,
    var searcher: SearcherInterface = AStarSearcher,
) {
    var gamePath: GamePath? = kotlin.run {
        PathFinder.getUpdatablePath(gameMap, searcher).toGamePathOrNull()
    }
        private set

    fun placeEntity(entity: MapEntity) {
        if (gamePath == null) {
            // If the path is currently null, then adding any entity won't change it
            return
        }

        if (!entity.isBlockingEntity) {
            // Entity is not capable of blocking path
            return
        }

        // TODO: Handle the case where the newly added entity is a pathing entity (e.g checkpoint).

        val currentGamePath = gamePath!!

        val intersectionResult =
            currentGamePath.getEntityPathThatIntersectsRectangle(entity.getRectangle())
        gameMap.placeEntity(entity)
        if (intersectionResult == null) {
            // Adding the new entity doesn't affect the path at all. OKAY to just add it!
            return
        } else {
            // Path is affected, recalulcate the whole path!
            val test = PathFinder.getUpdatablePath(gameMap)
            gamePath = test.toGamePathOrNull()
        }
        //
        //        gamePath = PathFinder.getUpdatablePath(gameMap)

        //        val remainingTeleports = currentGamePath.teleportsRemainingAt(intersectionResult.first)
        //        gameMap.placeEntity(entity)
        //
        //        val newPartialGamePath = searcher.getUpdatablePath(
        //            gameMap.width,
        //            gameMap.height,
        //            gameMap.getSequentialPathingEntities().drop(intersectionResult.first),
        //            gameMap.getCorrespondingTeleportPairs(remainingTeleports),
        //            gameMap.getBlockingEntities(),
        //            gameMap.blockingPointsView()
        //        )
        //
        //        if (newPartialGamePath == null) {
        //            gamePath = null
        //            return
        //        }
        //
        //        val existingPaths = currentGamePath.entityPaths.subList(0, intersectionResult.first)
        //        val existingInfos = currentGamePath.pathSequenceInfos.subList(0, intersectionResult.first)
        //
        //        val newGamePath = GamePath(
        //            existingPaths + newPartialGamePath.entityPaths,
        //            existingInfos + newPartialGamePath.pathSequenceInfos,
        //            currentGamePath.availableTeleports
        //        )
        //
        //        gamePath = newGamePath
    }

    fun placeEntities(vararg entities: MapEntity) {
        entities.forEach { placeEntity(it) }
    }
}

fun main() {
    val updater = PathUpdater(GameMap.create(10, 10))
}