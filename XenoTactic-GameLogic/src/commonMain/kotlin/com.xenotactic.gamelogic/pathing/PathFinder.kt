package pathing

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.measureTime


/**
 * Finds the shortest path for the given game map.
 */
object PathFinder {
    fun getUpdatablePath(gameMap: GameMap, searcher: SearcherInterface = AStarSearcher):
            GamePath? {
        return getUpdatablePathInternal(
            gameMap, gameMap.getBlockingEntities(), gameMap
                .blockingPointsView(), searcher = searcher
        )
    }

    fun getShortestPath(
        gameMap: GameMap,
        searcher: SearcherInterface = AStarSearcher
    ): PathSequence? {
        return getUpdatablePath(gameMap, searcher)?.toPathSequence()
    }

    fun getShortestPathWithBlockingEntities(
        gameMap: GameMap, blockingEntities: List<MapEntity>,
        searcher: SearcherInterface = AStarSearcher
    )
            : PathSequence? {
        return getUpdatablePathInternal(
            gameMap,
            gameMap.getBlockingEntities() + blockingEntities,
            null,
            searcher = searcher
        )?.toPathSequence()
    }

    fun getShortestPathWithTowers(
        gameMap: GameMap,
        towers: List<MapEntity.Tower>,
        searcher: SearcherInterface = AStarSearcher
    ):
            PathSequence? {
        return getUpdatablePathInternal(
            gameMap,
            gameMap.getBlockingEntities() + towers,
            null,
            searcher = searcher
        )?.toPathSequence()
    }

    fun getShortestPathWithTeleportPair(
        gameMap: GameMap, teleportPair: TeleportPair,
        searcher: SearcherInterface = AStarSearcher
    ): GamePath? {
        return getUpdatablePathInternal(
            gameMap, gameMap.getBlockingEntities(),
            gameMap.blockingPointsView(),
            additionalTeleportPairs = listOf(teleportPair),
            searcher = searcher
        )
    }

    fun getShortestPathOnPathingPoints(
        gameMap: GameMap,
        pathingEntities: List<MapEntity>,
        teleportPairs: List<TeleportPair> = gameMap.teleportPairs,
        searcher: SearcherInterface = AStarSearcher
    ): GamePath? {
        return getUpdatablePathInternal(
            gameMap,
            gameMap.getBlockingEntities(),
            pathingEntities = pathingEntities,
            teleportPairs = teleportPairs,
            searcher = searcher
        )
    }

    private fun getUpdatablePathInternal(
        gameMap: GameMap,
        blockingEntities: List<MapEntity>,
        blockingPoints: BlockingPointContainer.View? = null,
        pathingEntities: List<MapEntity> = gameMap.getSequentialPathingEntities(),
        teleportPairs: List<TeleportPair> = gameMap.teleportPairs,
        additionalTeleportPairs: List<TeleportPair> = emptyList(),
        searcher: SearcherInterface
    ):
            GamePath? {
        if (gameMap.getStart() == null || gameMap.getFinish() == null) return null

        return searcher.getUpdatablePath(
            gameMap.width, gameMap.height,
            pathingEntities,
            teleportPairs + additionalTeleportPairs,
            blockingEntities,
            blockingPoints
        )
    }
}