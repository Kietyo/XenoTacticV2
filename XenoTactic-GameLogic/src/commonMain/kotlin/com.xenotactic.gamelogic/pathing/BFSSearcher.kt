package pathing



import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.toGameUnitPoint
import com.xenotactic.gamelogic.pathing.*
import com.xenotactic.gamelogic.utils.measureTime


object BFSSearcher {
    class SearcherInternal(
        private val entitySequence: List<MapEntity>,
        private val blockingEntities: List<MapEntity>,
        private val availablePathingPoints: Set<IPoint>
    ) {
        var shortestPath: Path? = null
        var shortestPathLength = Double.MAX_VALUE
        var numStatesExplored = 0

        fun getPathInternal(
            currentIndex: Int,
            currentPath: Path,
            availablePathingPoints: Set<IPoint>
        ) {
            numStatesExplored++
            //        println("numStatesExplored: $numStatesExplored")
            if (currentIndex == entitySequence.size) {
                if (currentPath.pathLength < shortestPathLength) {
                    shortestPath = currentPath
                    shortestPathLength = currentPath.pathLength.toDouble()
                }
                return
            }

            if (currentPath.pathLength > shortestPathLength) {
                // No point in exploring further if the current path is already longer than the
                // shortest path found thus far.
                return
            }

            val lastPointOfCurrentPath = currentPath.getLastPoint().toPoint()

            val currentEntity = entitySequence[currentIndex]
            val points = listOf(currentEntity.centerPoint)

            // First just iterate through all points of the current entity to see if we can
            // go straight to that entity.
            var foundPath = false
            for (point in points) {
                if (lineIntersectsEntities(lastPointOfCurrentPath, point, blockingEntities)) {
                    continue
                }
                if (lastPointOfCurrentPath.distanceTo(point) + currentPath.pathLength.toDouble() >
                    shortestPathLength) {
                    continue
                }
                getPathInternal(
                    currentIndex + 1,
                    currentPath.addSegment(point),
                    this.availablePathingPoints
                )
                foundPath = true
            }

            if (!foundPath) {
                for (point in availablePathingPoints) {
                    if (lineIntersectsEntities(lastPointOfCurrentPath, point, blockingEntities)) {
                        continue
                    }

                    if (lastPointOfCurrentPath.distanceTo(point) + currentPath.pathLength.toDouble() >
                        shortestPathLength) {
                        continue
                    }

                    getPathInternal(
                        currentIndex,
                        currentPath.addSegment(point),
                        availablePathingPoints.minus(point)
                    )
                }
            }
        }
    }

    fun getShortestPath(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<MapEntity>,
        blockingEntities: List<MapEntity> = emptyList(),
        blockingPoints: BlockingPointContainer.View? = null
    ): PathSequence? {
        require(pathingEntities.size >= 2)
        val nonNullBlockingPoints =
            blockingPoints ?: BlockingPointContainer.View.create(blockingEntities)
        val startPoint = pathingEntities[0].centerPoint
        var availablePathingPoints: Set<IPoint> = setOf()
        measureTime("getting available pathing points") {
            availablePathingPoints =
                getAvailablePathingPointsFromBlockingEntities(
                    blockingEntities,
                    mapWidth,
                    mapHeight,
                    nonNullBlockingPoints
                ).points()
        }
        val sortedPathingPoints = availablePathingPoints.sortedWith(compareBy({
            it.x
        }, {
            it.y
        }))

        println(
            "availablePathingPoints (size=${sortedPathingPoints.size}): " +
                    "$sortedPathingPoints"
        )
        return measureTime("searching for path") {
            val searcher = SearcherInternal(
                pathingEntities.subList(1, pathingEntities.size), blockingEntities,
                availablePathingPoints
            )
            searcher.getPathInternal(
                0,
                Path(
                    listOf(startPoint.toGameUnitPoint())
                ),
                availablePathingPoints
            )

            if (searcher.shortestPath == null) null else PathSequence(
                listOf(
                    searcher
                        .shortestPath!!
                )
            )

        }.second
    }

}