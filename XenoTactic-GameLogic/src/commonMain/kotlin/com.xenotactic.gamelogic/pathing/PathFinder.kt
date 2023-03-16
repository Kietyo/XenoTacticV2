package pathing

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.GamePath
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.pathing.SearcherInterface
import com.xenotactic.gamelogic.utils.GameUnit


/**
 * Finds the shortest path for the given game map.
 */
object PathFinder {
    fun getUpdatablePath(
        gameMap: GameMap,
        searcher: SearcherInterface = AStarSearcher
    ): PathFindingResult {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getBlockingEntities(),
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
            searcher = searcher
        )
    }

    fun getShortestPath(
        gameMap: GameMap,
        searcher: SearcherInterface = AStarSearcher
    ): PathSequence? {
        return getUpdatablePath(gameMap, searcher).toGamePathOrNull()?.toPathSequence()
    }

    fun getShortestPathWithBlockingEntities(
        gameMap: GameMap, blockingEntities: List<MapEntity>,
    )
            : PathSequence? {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities() + blockingEntities,
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
        ).toGamePathOrNull()?.toPathSequence()
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
            gameMap.getBlockingEntities() + towers,
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
            searcher = searcher
        ).toGamePathOrNull()?.toPathSequence()
    }

    fun getShortestPathWithTeleportPair(
        gameMap: GameMap, teleportPair: TeleportPair,
    ): PathFindingResult {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            gameMap.getStart(),
            gameMap.getFinish(),
            gameMap.getBlockingEntities(),
            gameMap.getSequentialPathingEntities(),
            gameMap.teleportPairs,
            additionalTeleportPairs = listOf(teleportPair),
        )
    }

    fun getShortestPathOnPathingPoints(
        gameMap: GameMap,
        pathingEntities: List<IRectangleEntity>,
        teleportPairs: List<TeleportPair> = gameMap.teleportPairs,
        searcher: SearcherInterface = AStarSearcher
    ): GamePath? {
        return getUpdatablePath(
            gameMap.width,
            gameMap.height,
            blockingEntities = gameMap.getBlockingEntities(),
            pathingEntities = pathingEntities,
            teleportPairs = teleportPairs,
            searcher = searcher
        ).run {
            when (this) {
                is PathFindingResult.Failure -> null
                is PathFindingResult.Success -> this.gamePath
            }
        }
    }

    fun getUpdatablePath(
        width: GameUnit,
        height: GameUnit,
        start: IRectangleEntity?,
        finish: IRectangleEntity?,
        blockingEntities: List<IRectangleEntity> = emptyList(),
        pathingEntities: List<IRectangleEntity> = emptyList(),
        teleportPairs: List<TeleportPair> = emptyList(),
        additionalTeleportPairs: List<TeleportPair> = emptyList(),
    ): PathFindingResult {
        if (start == null) return PathFindingResult.Failure("Start is null")
        if (finish == null) return PathFindingResult.Failure("Finish is null")

        return getUpdatablePath(
            width, height,
            blockingEntities,
            mutableListOf<IRectangleEntity>().apply {
                add(start)
                addAll(pathingEntities)
                add(finish)
            },
            teleportPairs + additionalTeleportPairs,
        )
    }

    private fun getUpdatablePath(
        width: GameUnit,
        height: GameUnit,
        blockingEntities: List<IRectangleEntity> = emptyList(),
        pathingEntities: List<IRectangleEntity> = emptyList(),
        teleportPairs: List<TeleportPair> = emptyList(),
        searcher: SearcherInterface = AStarSearcher
    ): PathFindingResult {
        return searcher.getUpdatablePathV2(
            width.toInt(), height.toInt(),
            pathingEntities = pathingEntities,
            teleportPairs = teleportPairs,
            blockingEntities = blockingEntities
        )
    }
}