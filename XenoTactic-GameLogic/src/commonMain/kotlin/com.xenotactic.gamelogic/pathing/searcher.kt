package com.xenotactic.gamelogic.pathing

import korlibs.math.geom.*
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.getIntersectionPointsOfLineSegmentAndCircle
import com.xenotactic.gamelogic.utils.getIntersectionPointsOfLineSegmentAndRectangle
import com.xenotactic.gamelogic.utils.sumOf


enum class SearcherType {
    NONE,
    A_STAR_SEARCHER,
    A_STAR_SEARCHER_2
}

fun intersectSegments(
    p1: IPoint, p2: IPoint, rx1: Double, ry1: Double, rx2: Double, ry2: Double
): Boolean {
    val d = (ry2 - ry1) * (p2.x - p1.x) - (rx2 - rx1) * (p2.y - p1.y)
    if (d == 0.0) return false
    val yd = p1.y - ry1
    val xd = p1.x - rx1
    val ua = ((rx2 - rx1) * yd - (ry2 - ry1) * xd) / d
    if (ua < 0 || ua > 1) return false
    val ub = ((p2.x - p1.x) * yd - (p2.y - p1.y) * xd) / d
    if (ub < 0 || ub > 1) return false
    //    if (intersection != null) intersection.set(p1.x + (x2 - x1) * ua, p1.y + (p2.y - y1) * ua)
    return true
}

fun intersectSegmentRectangle(
    startPoint: IPoint,
    endPoint: IPoint,
    rectangle: IRectangle
): Boolean {
    val rectangleEndX = (rectangle.x + rectangle.width)
    val rectangleEndY = (rectangle.y + rectangle.height)
    if (intersectSegments(
            startPoint,
            endPoint,
            rectangle.x,
            rectangle.y,
            rectangle.x,
            rectangleEndY,
        )
    ) return true
    if (intersectSegments(
            startPoint,
            endPoint,
            rectangle.x,
            rectangle.y,
            rectangleEndX,
            rectangle.y,
        )
    ) return true
    if (intersectSegments(
            startPoint,
            endPoint,
            rectangleEndX,
            rectangle.y,
            rectangleEndX,
            rectangleEndY,
        )
    ) return true
    return if (intersectSegments(
            startPoint,
            endPoint,
            rectangle.x,
            rectangleEndY,
            rectangleEndX,
            rectangleEndY,
        )
    ) true else rectangle.contains(startPoint) || rectangle.contains(endPoint)
}

fun lineIntersectsEntity(p1: IPoint, p2: IPoint, entity: IRectangleEntity): Boolean {
    return intersectSegmentRectangle(p1, p2, entity.getRectangle())
}

sealed class PathAttribute {
    data class SpeedEffect(val speed: Double) : PathAttribute()
}

data class Segment(
    val point1: GameUnitTuple,
    val point2: GameUnitTuple,
    private val attributes: MutableList<PathAttribute> = mutableListOf()
) {
    constructor(p1: IPoint, p2: IPoint) : this(p1.toGameUnitPoint(), p2.toGameUnitPoint())

    val length = point1.distanceTo(point2)
    val effectiveLength: GameUnit
        get() {
            var speedEffect = 1.0
            for (attribute in attributes) {
                when (attribute) {
                    is PathAttribute.SpeedEffect -> speedEffect *= attribute.speed
                }
            }
            return length * (1.0 / speedEffect)
        }

    fun addAttribute(attribute: PathAttribute) {
        attributes.add(attribute)
    }

    fun intersectsRectangle(rect: IRectangle): Boolean {
        return intersectSegmentRectangle(point1.toPoint(), point2.toPoint(), rect)
    }

    fun getFirstIntersectionPointToCircle(circleCenter: IPoint, radius: Double): IPoint? {
        val points =
            getIntersectionPointsOfLineSegmentAndCircle(point1.toPoint(), point2.toPoint(), circleCenter, radius)
        if (points.isEmpty()) return null
        return points.minByOrNull { point1.distanceTo(it.toGameUnitPoint()) }
    }

    fun getFirstIntersectionPointToRectangle(rectBottomLeft: IPoint, width: Float, height: Float):
            IPoint? {
        val points =
            getIntersectionPointsOfLineSegmentAndRectangle(
                point1.toPoint(),
                point2.toPoint(),
                rectBottomLeft,
                width,
                height
            )
        if (points.isEmpty()) return null
        return points.minByOrNull { point1.distanceTo(it.toGameUnitPoint()) }
    }
}


data class Path(val points: List<GameUnitTuple>) {
    data class CircleIntersectionResult(val segmentIdx: Int, val intersectionPoint: GameUnitTuple)
    companion object {
        fun create(): Path {
            return Path(listOf())
        }

        fun create(vararg vectors: IPoint): Path {
            return Path(vectors.map { it.toGameUnitPoint() }.toList())
        }

        fun create(vararg vectors: Pair<Number, Number>): Path {
            return Path(vectors.map { GameUnitTuple(it.first.toDouble(), it.second.toDouble()) })
        }
    }

    val numPoints: Int
        get() = points.size

    val pathLength: GameUnit = run {
        var sum = GameUnit(0.0)
        var prevPoint: GameUnitTuple? = null
        for (p in points) {
            if (prevPoint == null) {
                prevPoint = p
            } else {
                sum += prevPoint!!.distanceTo(p)
                prevPoint = p
            }
        }
        sum
    }

    val numSegments: Int
        get() = points.size - 1

    fun getLastPoint(): GameUnitTuple {
        return points.last()
    }

    fun intersectsRectangle(rect: IRectangle): Boolean {
        return getSegments().any {
            it.intersectsRectangle(rect)
        }
    }

    fun getSegments(): List<Segment> {
        val segments = mutableListOf<Segment>()
        var previousPoint: GameUnitTuple? = null
        for (point in points) {
            if (previousPoint == null) {
                previousPoint = point
            } else {
                segments.add(Segment(previousPoint, point))
                previousPoint = point
            }
        }
        return segments.toList()
    }

    fun getFirstIntersectionPointToCircle(circleCenter: IPoint, radius: Double):
            CircleIntersectionResult? {
        val segments = getSegments()
        for ((idx, segment) in segments.withIndex()) {
            val intersect = segment.getFirstIntersectionPointToCircle(circleCenter, radius)
            if (intersect != null) {
                return CircleIntersectionResult(idx, intersect.toGameUnitPoint())
            }
        }
        return null
    }

    fun getFirstIntersectionPointToRectangle(rectBottomLeft: IPoint, width: Float, height: Float):
            CircleIntersectionResult? {
        val segments = getSegments()
        for ((idx, segment) in segments.withIndex()) {
            val intersect =
                segment.getFirstIntersectionPointToRectangle(rectBottomLeft, width, height)
            if (intersect != null) {
                return CircleIntersectionResult(idx, intersect.toGameUnitPoint())
            }
        }
        return null
    }
    fun addSegment(point: IPoint): Path = addSegment(point.toGameUnitPoint())
    fun addSegment(point: GameUnitTuple): Path {
        val newPoints = points.toMutableList()
        newPoints.add(point)
        return Path(newPoints)
    }

    fun getPathCutoffAtIntersection(intersect: CircleIntersectionResult): Path {
        require(intersect.segmentIdx < numSegments)
        require(intersect.segmentIdx >= 0)
        return Path(
            points.dropLast(points.size - (intersect.segmentIdx + 1)).plus(
                intersect.intersectionPoint
            )
        )
    }
}

data class PathSequence constructor(val paths: List<Path> = listOf()) {
    val pathLength: GameUnit = paths.sumOf { it.pathLength }
    val numPaths: Int
        get() = paths.size

    fun intersectsTower(tower: MapEntity.Tower): Boolean {
        return intersectsRectangle(tower.getRectangle())
    }

    fun intersectsRectangle(rect: IRectangle): Boolean {
        return paths.any {
            it.intersectsRectangle(rect)
        }
    }

    companion object {
        fun create(path: Path): PathSequence {
            return PathSequence(listOf(path))
        }

        fun create(vararg paths: Path): PathSequence {
            return PathSequence(paths.toList())
        }
    }
}



sealed class EntityPath {
    data class EntityToEntityPath(
        val startEntity: IRectangleEntity,
        val endEntity: IRectangleEntity,
        val path: Path
    ) : EntityPath()

    data class EntityToEntityIntersectsTeleport(
        val startEntity: IRectangleEntity,
        val endEntity: IRectangleEntity,
        val intersectingTeleport: Int,
        val startToEndPath: Path,
        val startToTeleportPath: Path
    ) : EntityPath()
}

data class PathSequenceInfo(
    // The integer represents the sequence number of the teleport
    val teleportsUsedForThisPath: Set<Int>,
) {
    companion object {
        val EMPTY = PathSequenceInfo(emptySet())
    }
}

/**
 * Represents a game path between various different entities.
 *
 * Suppose there are 3 pathing entities: Start -> CP1 -> Finish
 *
 * Then
 * -
 * - pathSequence[0] refers to the path sequence from Start -> CP1
 * - pathSequence[1] refers to the path sequence from CP1 -> Finish
 */
//data class GamePath(
//    val entityPaths: List<PathSequence>,
//    //    private val pathSequenceInfos: List<PathSequenceInfo>,
//    //    val availableTeleports: Set<Int>
//) {
//    val pathLength: Double = entityPaths.sumOf { it.pathLength }
//    val numPathingEntities: Int = when {
//        entityPaths.isEmpty() -> 0
//        else -> entityPaths.size + 1
//    }
//
//    /**
//     * Returns the teleports that are remaining at the given index.
//     *
//     * For example if we had 3 entity paths A -> B -> C -> D and the teleport usage looks like this:
//     * [[1, 2], [0], [3, 4]]
//     *
//     * This means that:
//     * - From A -> B, teleports 1 and 2 were used
//     * - From B -> C, teleport 0 was used
//     * - From C -> D, teleports 3 and 4 were used
//     *
//     * Then:
//     * - teleportsRemainingAt(0) = [0, 1, 2, 3, 4]
//     * - teleportsRemainingAt(1) = [0, 3, 4]
//     * - teleportsRemainingAt(2) = [3, 4]
//     */
//    //    fun teleportsRemainingAt(idx: Int): Set<Int> {
//    //        return availableTeleports - pathSequenceInfos.take(idx).flatMap {
//    //            it
//    //                .teleportsUsedForThisPath
//    //        }.toSet()
//    //    }
//
//    /**
//     * Returns null if nothing intersects the given rectangle or a pair where:
//     * - first -> index of the path sequence
//     * - second -> the path sequence that first intersected the rectangle
//     */
//    fun getPathSequenceThatIntersectsRectangle(rect: Rectangle): Pair<Int, PathSequence>? {
//        for ((i, pathSequence) in entityPaths.withIndex()) {
//            if (pathSequence.intersectsRectangle(rect)) {
//                return Pair(i, pathSequence)
//            }
//        }
//        return null
//    }
//
//    fun intersectsRectangle(rect: Rectangle): Boolean {
//        return entityPaths.any {
//            it.intersectsRectangle(rect)
//        }
//    }
//
//    fun toPathSequence(): PathSequence {
//        return PathSequence(entityPaths.flatMap { it.paths })
//    }
//
//    companion object {
//        fun create(pathSequences: List<PathSequence>): GamePath {
//            return GamePath(
//                pathSequences,
//                //                pathSequences.map { PathSequenceInfo.EMPTY }, emptySet()
//            )
//        }
//    }
//}

sealed class PathFindingResult {
    data class Success(val gamePath: GamePath) : PathFindingResult()
    data class Failure(val errorMessage: String) : PathFindingResult()

    fun toGamePathOrNull(): GamePath? {
        return when (this) {
            is Failure -> null
            is Success -> gamePath
        }
    }
}

/**
 * Represents a game path between various different entities.
 *
 * Suppose there are 3 pathing entities: Start -> CP1 -> Finish
 *
 * Then
 * -
 * - pathSequence[0] refers to the path sequence from Start -> CP1
 * - pathSequence[1] refers to the path sequence from CP1 -> Finish
 */
data class GamePath(
    val entityPaths: List<EntityPath>,
    //    val pathSequenceInfos: List<PathSequenceInfo>,
    //    val availableTeleports: Set<Int>
) {
    val pathLength: GameUnit = entityPaths.sumOf {
        when (it) {
            is EntityPath.EntityToEntityPath -> it.path.pathLength
            is EntityPath.EntityToEntityIntersectsTeleport -> it.startToTeleportPath.pathLength
        }
    }
    //    val numPathingEntities: Int = when {
    //        entityPaths.isEmpty() -> 0
    //        else -> entityPaths.size + 1
    //    }

    fun getEntityPathThatIntersectsRectangle(rect: IRectangle): Pair<Int, EntityPath>? {
        for ((i, entityPath) in entityPaths.withIndex()) {
            when (entityPath) {
                is EntityPath.EntityToEntityPath -> {
                    if (entityPath.path.intersectsRectangle(rect)) {
                        return Pair(i, entityPath)
                    }
                }
                is EntityPath.EntityToEntityIntersectsTeleport -> {
                    if (entityPath.startToEndPath.intersectsRectangle(rect)) {
                        return Pair(i, entityPath)
                    }
                }
            }
        }
        return null
    }

    /**
     * Returns the teleports that are remaining at the given index.
     *
     * For example if we had 3 entity paths A -> B -> C -> D and the teleport usage looks like this:
     * [[1, 2], [0], [3, 4]]
     *
     * This means that:
     * - From A -> B, teleports 1 and 2 were used
     * - From B -> C, teleport 0 was used
     * - From C -> D, teleports 3 and 4 were used
     *
     * Then:
     * - teleportsRemainingAt(0) = [0, 1, 2, 3, 4]
     * - teleportsRemainingAt(1) = [0, 3, 4]
     * - teleportsRemainingAt(2) = [3, 4]
     */
    //    fun teleportsRemainingAt(idx: Int): Set<Int> {
    //        return availableTeleports - pathSequenceInfos.take(idx).flatMap {
    //            it
    //                .teleportsUsedForThisPath
    //        }.toSet()
    //    }

    fun intersectsRectangle(rect: IRectangle): Boolean {
        return entityPaths.any {
            when (it) {
                is EntityPath.EntityToEntityPath -> it.path.intersectsRectangle(rect)
                is EntityPath.EntityToEntityIntersectsTeleport -> it.startToEndPath
                    .intersectsRectangle(rect) || it.startToTeleportPath.intersectsRectangle(rect)
            }
        }
    }

    fun toPathSequence(): PathSequence {
        return PathSequence(entityPaths.map {
            when (it) {
                is EntityPath.EntityToEntityPath -> it.path
                is EntityPath.EntityToEntityIntersectsTeleport -> it.startToTeleportPath
            }
        })
    }

}

fun lineIntersectsEntities(p1: IPoint, p2: IPoint, entities: List<IRectangleEntity>): Boolean {
    return entities.any { lineIntersectsEntity(p1, p2, it) }
}

data class TeleportIntersectionCandidate(
    val sequenceNumber: Int,
    val circleIntersectionResult: Path.CircleIntersectionResult
)