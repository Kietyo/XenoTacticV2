package pathing

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.RectangleEntity
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
        return getUpdatablePathInternal(
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
        return getUpdatablePathInternal(
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
        return getUpdatablePathInternal(
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
        return getUpdatablePathInternal(
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
        return getUpdatablePathInternal(
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

    private fun getUpdatablePathInternal(
        width: Int,
        height: Int,
        start: RectangleEntity?,
        finish: RectangleEntity?,
        blockingEntities: List<RectangleEntity>,
        blockingPoints: BlockingPointContainer.View? = null,
        pathingEntities: List<RectangleEntity>,
        teleportPairs: List<TeleportPair>,
        additionalTeleportPairs: List<TeleportPair> = emptyList(),
        searcher: SearcherInterface
    ): GamePath? {
        if (start == null || finish == null) return null

        return searcher.getUpdatablePath(
            width, height,
            pathingEntities,
            teleportPairs + additionalTeleportPairs,
            blockingEntities,
            blockingPoints
        )
    }
}