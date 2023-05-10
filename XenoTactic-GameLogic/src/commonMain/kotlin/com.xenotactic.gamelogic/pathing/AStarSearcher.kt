package pathing


import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.utils.PATHING_RADIUS
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.model.toGameUnitPoint
import com.xenotactic.gamelogic.pathing.*
import com.xenotactic.gamelogic.utils.IntStatCounter
import korlibs.datastructure.PriorityQueue
import utils.PathingPointUtil
import kotlin.math.sign

object AStarSearcher : SearcherInterface {
    private val _counter = IntStatCounter("a star")

    override fun getUpdatablePath(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<IRectangleEntity>,
        teleportPairs: List<TeleportPair>,
        blockingEntities: List<IRectangleEntity>,
    ): GamePath? {
        return getUpdatablePathV2(mapWidth, mapHeight, pathingEntities, teleportPairs, blockingEntities).toGamePathOrNull()
    }

    override fun getUpdatablePathV2(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<IRectangleEntity>,
        teleportPairs: List<TeleportPair>,
        blockingEntities: List<IRectangleEntity>
    ): PathFindingResult {
        val nonNullBlockingPoints = BlockingPointContainer.View.create(blockingEntities)
        return FullPathSearcherInternal(
            mapWidth,
            mapHeight,
            blockingEntities,
            nonNullBlockingPoints
        ).getShortestPath(
            pathingEntities,
            teleportPairs,
        )
    }

    class FullPathSearcherInternal(
        mapWidth: Int,
        mapHeight: Int,
        private val _blockingEntities: List<IRectangleEntity>,
        private val _blockingPoints: BlockingPointContainer.View
    ) {
        private val cachedShortestPoints: MutableMap<Pair<IPoint, IPoint>, IPoint> =
            mutableMapOf()

        val availablePathingPoints = getAvailablePathingPointsFromBlockingEntities(
            _blockingEntities,
            mapWidth,
            mapHeight,
            _blockingPoints
        )

        private fun getFirstTeleportIntersection(
            shortestPath: Path?,
            teleportPairs: List<TeleportPair>,
            activatedTeleportsThusFar: Set<Int>,
            teleportsUsedForThisPathSequence: Set<Int>
        ): TeleportIntersectionCandidate? {
            if (shortestPath == null) return null
            val intersectionPointCandidates =
                mutableListOf<TeleportIntersectionCandidate>()
            for (teleportPair in teleportPairs) {
                if (activatedTeleportsThusFar.contains(teleportPair.sequenceNumber) ||
                    teleportsUsedForThisPathSequence.contains(teleportPair.sequenceNumber)
                )
                    continue
                val intersectionPoint =
                    shortestPath.getFirstIntersectionPointToRectangle(
                        teleportPair.teleportIn.bottomLeftUnitSquareGameUnitPoint.toPoint(),
                        teleportPair.teleportIn.width.value.toFloat(),
                        teleportPair.teleportIn.height.value.toFloat(),
                    )
                if (intersectionPoint != null) {
                    intersectionPointCandidates.add(
                        TeleportIntersectionCandidate(
                            teleportPair.sequenceNumber,
                            intersectionPoint
                        )
                    )
                }
            }

            if (intersectionPointCandidates.isEmpty()) {
                return null
            }

            intersectionPointCandidates.sortWith(
                compareBy<TeleportIntersectionCandidate> { it.circleIntersectionResult.segmentIdx }
                    .thenBy {
                        shortestPath.getSegments().get(
                            it.circleIntersectionResult
                                .segmentIdx
                            // TODO: Can use dst^2 to improve performance.
                        ).point1.distanceTo(it.circleIntersectionResult.intersectionPoint)
                    }
            )

            return intersectionPointCandidates.firstOrNull()
        }

        private fun failure(errorMessage: String): PathFindingResult.Failure {
            return PathFindingResult.Failure(Throwable(errorMessage).stackTraceToString())
        }

        fun getShortestPath(
            pathingEntities: List<IRectangleEntity>,
            teleportPairs: List<TeleportPair>,
        ): PathFindingResult {
            if (pathingEntities.size < 2) {
                return failure("Requires at least 2 pathing entities.")
            }
            val sequenceNumToTeleportPair = teleportPairs.groupBy { it.sequenceNumber }.mapValues {
                it.value.first()
            }

            var prevEntity: IRectangleEntity? = null
            val entityPaths = mutableListOf<EntityPath>()
            val pathSequenceInfos = mutableListOf<PathSequenceInfo>()
            val activatedTeleportsThusFar = mutableSetOf<Int>()
            for (entity in pathingEntities) {
                if (prevEntity == null) {
                    prevEntity = entity
                    continue
                }
                val teleportsUsedForThisPathSequence = mutableSetOf<Int>()
                var shortestPath =
                    getShortestPathFromStartToFinish(
                        prevEntity,
                        entity,
                        _blockingEntities,
                        _blockingPoints
                    )


                // Check to see if the path intersects with any teleports
                while (true) {
                    if (shortestPath == null) return failure("""
                        No path found to start with.
                            prevEntity: $prevEntity,
                            entity: $entity
                            pathingEntities: $pathingEntities
                    """.trimIndent())

                    val teleportCandidate = getFirstTeleportIntersection(
                        shortestPath, teleportPairs, activatedTeleportsThusFar,
                        teleportsUsedForThisPathSequence
                    )

                    if (teleportCandidate == null) {
                        entityPaths.add(
                            EntityPath.EntityToEntityPath(prevEntity!!, entity, shortestPath)
                        )
                        break
                    }

                    teleportsUsedForThisPathSequence.add(teleportCandidate.sequenceNumber)

                    entityPaths.add(
                        EntityPath.EntityToEntityIntersectsTeleport(
                            prevEntity!!, entity,
                            teleportCandidate.sequenceNumber,
                            shortestPath,
                            shortestPath.getPathCutoffAtIntersection(
                                teleportCandidate.circleIntersectionResult
                            )
                        )
                    )

                    prevEntity =
                        sequenceNumToTeleportPair[teleportCandidate.sequenceNumber]!!.teleportOut
                    shortestPath =
                        getShortestPathFromStartToFinish(
                            prevEntity,
                            entity,
                            _blockingEntities,
                            _blockingPoints
                        )
                }

                pathSequenceInfos.add(
                    PathSequenceInfo(
                        teleportsUsedForThisPathSequence,
                    )
                )

                activatedTeleportsThusFar.addAll(teleportsUsedForThisPathSequence)

                prevEntity = entity
            }

            return PathFindingResult.Success(GamePath(
                entityPaths,
            ))
        }

        private fun getShortestPathFromStartToFinish(
            start: IRectangleEntity, finish: IRectangleEntity,
            blockingEntities: List<IRectangleEntity>,
            blockingPoints: BlockingPointContainer.View
        ): Path? {
            val endCenter = finish.centerPoint

            val cameFrom = mutableMapOf<IPoint, IPoint>()
            val costSoFar = mutableMapOf<IPoint, Double>()
            val frontier =
                PriorityQueue<SearchNode> { o1, o2 -> (o1.priority - o2.priority).sign.toInt() }

            val startingPoints = PathingPointUtil().calculate(
                start, blockingPoints
            )

            for (shortestStartingPoint in startingPoints) {
                frontier.add(SearchNode(shortestStartingPoint, 0.0, setOf(shortestStartingPoint)))
                costSoFar[shortestStartingPoint] = 0.0
            }

            val endPoints = PathingPointUtil().calculate(finish, blockingPoints)

            var nodesSearched = 0
            while (frontier.isNotEmpty()) {
                val current = frontier.removeHead()

                if (endPoints.contains(current.point)) break

                val validEndPoints = endPoints.filter {
                    !lineIntersectsEntities(current.point, it, blockingEntities)
                }

                val nextPathingPoints = validEndPoints.ifEmpty {
                    getNextPoints(
                        current.point,
                        blockingEntities,
                        availablePathingPoints
                    )
                }

                for (pathingPoint in nextPathingPoints) {
                    if (current.exploredPoints.contains(pathingPoint)) continue
                    nodesSearched++
                    val newCost =
                        costSoFar[current.point]!! + current.point.distanceTo(pathingPoint)
                    if (!costSoFar.containsKey(pathingPoint) || newCost < costSoFar[pathingPoint]!!) {
                        costSoFar[pathingPoint] = newCost
                        val shortestPointAtEndToPathingPoint =
                            calculateShortestPointFromStartToEndCircleCached(
                                pathingPoint,
                                endCenter,
                                PATHING_RADIUS
                            )

                        val priority = newCost + if (lineIntersectsEntities(
                                pathingPoint,
                                shortestPointAtEndToPathingPoint, blockingEntities
                            )
                        ) 0.0 else
                            pathingPoint.distanceTo(shortestPointAtEndToPathingPoint)
                        frontier.add(
                            SearchNode(
                                pathingPoint, priority, current.exploredPoints +
                                        pathingPoint
                            )
                        )
                        cameFrom[pathingPoint] = current.point
                    }
                }
            }

            _counter.record(nodesSearched)

            for (allowedEndPoint in endPoints) {
                if (cameFrom.containsKey(allowedEndPoint)) {
                    val resultingPath = mutableListOf<IPoint>()
                    var currentPoint = allowedEndPoint
                    while (!startingPoints.contains(currentPoint)) {
                        resultingPath.add(currentPoint)
                        currentPoint = cameFrom[currentPoint]!!
                    }
                    resultingPath.add(currentPoint)
                    resultingPath.reverse()

                    return Path(resultingPath.map { it.toGameUnitPoint() })
                }
            }

            return null
        }

        private fun calculateShortestPointFromStartToEndCircleCached(
            point: IPoint,
            circleCenter: IPoint,
            radius: Double
        ): IPoint {
            return cachedShortestPoints.getOrPut(
                Pair(point, circleCenter)
            ) {
                calculateShortestPointFromStartToEndCircle(
                    point,
                    circleCenter,
                    radius
                )
            }
        }
    }

    data class SearchNode(val point: IPoint, val priority: Double, val exploredPoints: Set<IPoint>)
}
