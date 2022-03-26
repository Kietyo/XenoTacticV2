package com.xenotactic.gamelogic.pathing

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.contains
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.getIntersectionPointsOfLineSegmentAndCircle
import com.xenotactic.gamelogic.utils.getIntersectionPointsOfLineSegmentAndRectangle


enum class SearcherType {
    NONE,
    A_STAR_SEARCHER,
    A_STAR_SEARCHER_2
}

fun intersectSegments(
    p1: Point, p2: Point, rx1: Double, ry1: Double, rx2: Double, ry2: Double
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
    startPoint: Point,
    endPoint: Point,
    rectangle: Rectangle
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

fun lineIntersectsEntity(p1: Point, p2: Point, entity: MapEntity): Boolean {
    return intersectSegmentRectangle(p1, p2, entity.getRectangle())
}

sealed class PathAttribute {
    data class SpeedEffect(val speed: Double) : PathAttribute()
}

data class Segment(
    val point1: Point,
    val point2: Point,
    private val attributes: MutableList<PathAttribute> = mutableListOf()
) {
    val length = point1.distanceTo(point2)
    val effectiveLength: Double
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

    fun intersectsRectangle(rect: Rectangle): Boolean {
        return intersectSegmentRectangle(point1, point2, rect)
    }

    fun getFirstIntersectionPointToCircle(circleCenter: Point, radius: Double): Point? {
        val points =
            getIntersectionPointsOfLineSegmentAndCircle(point1, point2, circleCenter, radius)
        if (points.isEmpty()) return null
        return points.minByOrNull { point1.distanceTo(it) }
    }

    fun getFirstIntersectionPointToRectangle(rectBottomLeft: Point, width: Float, height: Float):
            Point? {
        val points =
            getIntersectionPointsOfLineSegmentAndRectangle(
                point1,
                point2,
                rectBottomLeft,
                width,
                height
            )
        if (points.isEmpty()) return null
        return points.minByOrNull { point1.distanceTo(it) }
    }
}

data class Path(val points: List<Point>) {
    data class CircleIntersectionResult(val segmentIdx: Int, val intersectionPoint: Point)
    companion object {
        fun create(): Path {
            return Path(listOf())
        }

        fun create(vararg vectors: Point): Path {
            return Path(vectors.toList())
        }

        fun create(vararg vectors: Pair<Number, Number>): Path {
            return Path(vectors.map { Point(it.first.toDouble(), it.second.toDouble()) })
        }
    }

    val numPoints: Int
        get() = points.size

    val pathLength: Double = run {
        var sum = 0.0
        var prevPoint: Point? = null
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

    fun getLastPoint(): Point {
        return points.last()
    }

    fun intersectsRectangle(rect: Rectangle): Boolean {
        return getSegments().any {
            it.intersectsRectangle(rect)
        }
    }

    fun getSegments(): List<Segment> {
        val segments = mutableListOf<Segment>()
        var previousPoint: Point? = null
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

    fun getFirstIntersectionPointToCircle(circleCenter: Point, radius: Double):
            CircleIntersectionResult? {
        val segments = getSegments()
        for ((idx, segment) in segments.withIndex()) {
            val intersect = segment.getFirstIntersectionPointToCircle(circleCenter, radius)
            if (intersect != null) {
                return CircleIntersectionResult(idx, intersect)
            }
        }
        return null
    }

    fun getFirstIntersectionPointToRectangle(rectBottomLeft: Point, width: Float, height: Float):
            CircleIntersectionResult? {
        val segments = getSegments()
        for ((idx, segment) in segments.withIndex()) {
            val intersect =
                segment.getFirstIntersectionPointToRectangle(rectBottomLeft, width, height)
            if (intersect != null) {
                return CircleIntersectionResult(idx, intersect)
            }
        }
        return null
    }

    fun addSegment(point: Point): Path {
        val newPoints = points.toMutableList()
        newPoints.add(point)
        return Path(newPoints)
    }

    fun getPathCutoffAtIntersection(intersect: CircleIntersectionResult): Path {
        require(intersect.segmentIdx < numSegments)
        require(intersect.segmentIdx >= 0)
        return Path(
            points.dropLast(points.size - (intersect.segmentIdx + 1)).plus(
                intersect
                    .intersectionPoint
            )
        )
    }
}

data class PathSequence constructor(val paths: List<Path> = listOf()) {
    val pathLength: Double = paths.sumOf { it.pathLength }
    val numPaths: Int
        get() = paths.size

    fun intersectsTower(tower: MapEntity.Tower): Boolean {
        return intersectsRectangle(Rectangle(tower.x, tower.y, tower.width, tower.height))
    }

    fun intersectsRectangle(rect: Rectangle): Boolean {
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
        val startEntity: MapEntity,
        val endEntity: MapEntity,
        val path: Path
    ) : EntityPath()

    data class EntityToEntityIntersectsTeleport(
        val startEntity: MapEntity,
        val endEntity: MapEntity,
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
    val pathLength: Double = entityPaths.sumOf {
        when (it) {
            is EntityPath.EntityToEntityPath -> it.path.pathLength
            is EntityPath.EntityToEntityIntersectsTeleport -> it.startToTeleportPath.pathLength
        }
    }
    //    val numPathingEntities: Int = when {
    //        entityPaths.isEmpty() -> 0
    //        else -> entityPaths.size + 1
    //    }

    fun getEntityPathThatIntersectsRectangle(rect: Rectangle): Pair<Int, EntityPath>? {
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

    fun intersectsRectangle(rect: Rectangle): Boolean {
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

fun lineIntersectsEntities(p1: Point, p2: Point, entities: List<MapEntity>): Boolean {
    return entities.any { lineIntersectsEntity(p1, p2, it) }
}

data class TeleportIntersectionCandidate(
    val sequenceNumber: Int,
    val circleIntersectionResult: Path.CircleIntersectionResult
)