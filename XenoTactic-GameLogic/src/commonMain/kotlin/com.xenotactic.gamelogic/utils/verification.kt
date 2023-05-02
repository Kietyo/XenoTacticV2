import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.TeleportPair
import pathing.PathFinder

sealed class MapVerificationResult {
    object Success : MapVerificationResult()
    data class Failure(
        val error: String
    ) : MapVerificationResult()
}

/**
 * Verify that the map is good for playing on.
 * Returns true if good, false if bad.
 */
fun GameMap.verify(): MapVerificationResult {
    val start = getStart() ?: return MapVerificationResult.Failure("Missing start")
    getFinish() ?: return MapVerificationResult.Failure("Missing finish")

    val blockingEntities = this.getBlockingEntities()
    val pathingEntities = getPathingEntities()

    pathingEntities.forEach {
        // None of the pathing entities should be fully covered
        if (it.isFullyCoveredBy(blockingEntities)) return MapVerificationResult.Failure(
            "Pathing entity is fully covered by a blocking entity"
        )
    }

    this.teleportPairs.forEach {
        pathingEntities.forEach { pathingEntity ->
            if (it.teleportIn.isFullyCoveredBy(pathingEntity))
                return MapVerificationResult.Failure("Teleport in ${it.teleportIn} is fully " +
                        "covered by pathing entity: $pathingEntity"
                )
        }

        // No blocking entities should fully cover the teleport out
        if (it.teleportOut.isFullyCoveredBy(blockingEntities)) {
            return MapVerificationResult.Failure(
                "Teleport out ${it.teleportOut} is fully covered by blocking entity"
            )
        }

        // Make sure that all teleport outs are not blocked
        PathFinder.getShortestPathOnPathingPoints(
            this, listOf(
                start,
                it.teleportOut,
            ),
            teleportPairs = emptyList<TeleportPair>()
        ) ?: return MapVerificationResult.Failure(
            "Teleport ${it.teleportOut} is unreachable"
        )
    }

    PathFinder.getShortestPath(this) ?: return MapVerificationResult.Failure(
        "No path available"
    )

    return MapVerificationResult.Success
}