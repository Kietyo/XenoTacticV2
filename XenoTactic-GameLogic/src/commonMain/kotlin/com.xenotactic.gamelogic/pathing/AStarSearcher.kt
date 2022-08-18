package pathing

import com.soywiz.kds.PriorityQueue
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.globals.PATHING_RADIUS
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.PathingBlockingEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.*
import com.xenotactic.gamelogic.utils.horizontalDirectionTo
import com.xenotactic.gamelogic.utils.measureTime
import com.xenotactic.gamelogic.utils.verticalDirectionTo
import com.xenotactic.gamelogic.utils.IntStatCounter
import utils.PathingPointUtil
import kotlin.math.sign

object AStarSearcher : SearcherInterface {
    private val _counter = IntStatCounter("a star")

    fun getNextPoints(
        currentPoint: Point,
        blockingEntities: List<PathingBlockingEntity>,
        availablePathingPoints: Set<PathingPoint>,
    ): List<Point> {
        return availablePathingPoints.mapNotNull {
            val verticalDirectionToPathingPoint = currentPoint.verticalDirectionTo(it.v)
            val horizontalDirectionToPathingPoint = currentPoint.horizontalDirectionTo(it.v)
            if ((it.entityVerticalDirection == VerticalDirection.DOWN &&
                        it.entityHorizontalDirection == HorizontalDirection.LEFT &&
                        verticalDirectionToPathingPoint == VerticalDirection.UP &&
                        horizontalDirectionToPathingPoint == HorizontalDirection.RIGHT) ||
                (it.entityVerticalDirection == VerticalDirection.DOWN &&
                        it.entityHorizontalDirection == HorizontalDirection.RIGHT &&
                        verticalDirectionToPathingPoint == VerticalDirection.UP &&
                        horizontalDirectionToPathingPoint == HorizontalDirection.LEFT) ||
                (it.entityVerticalDirection == VerticalDirection.UP &&
                        it.entityHorizontalDirection == HorizontalDirection.LEFT &&
                        verticalDirectionToPathingPoint == VerticalDirection.DOWN &&
                        horizontalDirectionToPathingPoint == HorizontalDirection.RIGHT) ||
                (it.entityVerticalDirection == VerticalDirection.UP &&
                        it.entityHorizontalDirection == HorizontalDirection.RIGHT &&
                        verticalDirectionToPathingPoint == VerticalDirection.DOWN &&
                        horizontalDirectionToPathingPoint == HorizontalDirection.LEFT) ||
                lineIntersectsEntities(
                    currentPoint,
                    it.v,
                    blockingEntities
                )
            ) {
                null
            } else {
                it.v
            }
        }
    }

    override fun getUpdatablePath(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<MapEntity>,
        teleportPairs: List<TeleportPair>,
        blockingEntities: List<PathingBlockingEntity>,
        blockingPoints: BlockingPointContainer.View?
    ): GamePath? {
        val nonNullBlockingPoints =
            blockingPoints ?: BlockingPointContainer.View.create(blockingEntities)
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
        private val _blockingEntities: List<PathingBlockingEntity>,
        private val _blockingPoints: BlockingPointContainer.View
    ) {
        private val cachedShortestPoints: MutableMap<Pair<Point, Point>, Point> =
            mutableMapOf<Pair<Point, Point>, Point>()

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
                        teleportPair.teleportIn.bottomLeftUnitSquareIntPoint.toPoint(),
                        teleportPair.teleportIn.width.toFloat(),
                        teleportPair.teleportIn.height.toFloat(),
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

        fun getShortestPath(
            pathingEntities: List<MapEntity>,
            teleportPairs: List<TeleportPair>,
        ): GamePath? {
            if (pathingEntities.size < 2) {
                return null
            }
            val sequenceNumToTeleportPair = teleportPairs.groupBy { it.sequenceNumber }.mapValues {
                it.value.first()
            }

            var prevEntity: MapEntity? = null
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
                    if (shortestPath == null) return null

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

            return GamePath(
                entityPaths,
            )
        }

        private fun getShortestPathFromStartToFinish(
            start: MapEntity, finish: MapEntity,
            blockingEntities: List<PathingBlockingEntity>,
            blockingPoints: BlockingPointContainer.View
        ): Path? {
            val endCenter = finish.centerPoint

            val cameFrom = mutableMapOf<Point, Point>()
            val costSoFar = mutableMapOf<Point, Double>()
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
                    val resultingPath = mutableListOf<Point>()
                    var currentPoint = allowedEndPoint
                    while (!startingPoints.contains(currentPoint)) {
                        resultingPath.add(currentPoint)
                        currentPoint = cameFrom[currentPoint]!!
                    }
                    resultingPath.add(currentPoint)
                    resultingPath.reverse()

                    return Path(resultingPath)
                }
            }

            return null
        }

        private fun calculateShortestPointFromStartToEndCircleCached(
            point: Point,
            circleCenter: Point,
            radius: Double
        ): Point {
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

    data class SearchNode(val point: Point, val priority: Double, val exploredPoints: Set<Point>)
}
