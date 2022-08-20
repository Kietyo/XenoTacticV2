package pathing

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.pathing.SearcherInterface


/**
 * Finds the shortest path for the given game map.
 */
object PathFinder {
    fun getUpdatablePath(gameMap: GameMap, searcher: SearcherInterface = AStarSearcher):
            GamePath? {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities(),
            gameMap
                .blockingPointsView(),
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
            searcher = searcher
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
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities() + blockingEntities,
            null,
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
            searcher = searcher
        )?.toPathSequence()
    }

    fun getShortestPathWithTowers(
        gameMap: GameMap,
        towers: List<MapEntity.Tower>,
        searcher: SearcherInterface = AStarSearcher
    ):
            PathSequence? {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities() + towers,
            null,
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
            searcher = searcher
        )?.toPathSequence()
    }

    fun getShortestPathWithTeleportPair(
        gameMap: GameMap, teleportPair: TeleportPair,
        searcher: SearcherInterface = AStarSearcher
    ): GamePath? {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities(),
            gameMap.blockingPointsView(),
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
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
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities(),
            pathingEntities = pathingEntities,
            teleportPairs = teleportPairs,
            searcher = searcher
        )
    }

    fun getUpdatablePath(
        width: Int,
        height: Int,
        start: IRectangleEntity?,
        finish: IRectangleEntity?,
        blockingEntities: List<IRectangleEntity> = emptyList(),
        blockingPoints: BlockingPointContainer.View? = null,
        pathingEntities: List<IRectangleEntity> = emptyList(),
        teleportPairs: List<TeleportPair> = emptyList(),
        additionalTeleportPairs: List<TeleportPair> = emptyList(),
        searcher: SearcherInterface = AStarSearcher
    ): GamePath? {
        if (start == null || finish == null) return null

        return searcher.getUpdatablePath(
            width, height,
            mutableListOf<IRectangleEntity>().apply {
                add(start)
                addAll(pathingEntities)
                add(finish)
            },
            teleportPairs + additionalTeleportPairs,
            blockingEntities,
            blockingPoints
        )
    }
}