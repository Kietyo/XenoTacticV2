package pathing

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.pathing.Segment
import com.xenotactic.gamelogic.utils.lerp

class SegmentTraversal(private val segment: Segment) {
    var distanceTraversed = 0.0
    var currentPosition = segment.point1.copy()
        private set

    // This is only set once we've traversed passed the current segment length
    var nonTraversedDistance = 0.0
        private set

    fun traverse(distance: Double) {
        if (finishedTraversal()) return
        val newDistanceTraversed = distanceTraversed + distance
        if (newDistanceTraversed < segment.length) {
            distanceTraversed = newDistanceTraversed
            currentPosition = segment.point1.lerp(
                segment.point2, newDistanceTraversed /
                        segment.length
            )
        } else {
            nonTraversedDistance = (distance + distanceTraversed) - segment.length
            distanceTraversed = segment.length
            currentPosition = segment.point2
        }
    }

    fun finishedTraversal(): Boolean {
        return distanceTraversed >= segment.length
    }
}

class PathTraversal(val path: Path) {
    var distanceTraversed = 0.0
        private set
    private val segments = path.getSegments()
    private var currentSegmentIdx = 0
    private var currentSegmentTraversal = SegmentTraversal(segments.first())
    val currentPosition: Point
        get() = currentSegmentTraversal.currentPosition

    val nonTraversedDistance: Double
        get() = currentSegmentTraversal.nonTraversedDistance

    /**
     * Traverses the path by the given distance.
     * - If we've already finish traversing the path then nothing is done.
     * - If the provided distance traverses past the path, then any non traversed distance will
     *   be stored in the `nonTraversedDistance` field.
     */
    fun traverse(distance: Double) {
        if (finishedTraversal()) return
        var remainingDistanceToTraverse = distance

        while (remainingDistanceToTraverse > 0.0f) {
            currentSegmentTraversal.traverse(remainingDistanceToTraverse)
            distanceTraversed += remainingDistanceToTraverse - currentSegmentTraversal.nonTraversedDistance
            remainingDistanceToTraverse = currentSegmentTraversal.nonTraversedDistance

            if (currentSegmentTraversal.finishedTraversal()) {
                if (isLastSegment()) return
                currentSegmentIdx++
                currentSegmentTraversal = SegmentTraversal(segments[currentSegmentIdx])
            }
        }
    }

    fun finishedTraversal(): Boolean {
        return isLastSegment() && currentSegmentTraversal.finishedTraversal()
    }

    private fun isLastSegment(): Boolean {
        return currentSegmentIdx == (segments.size - 1)
    }
}

class PathSequenceTraversal(pathSequence: PathSequence) {
    var distanceTraversed = 0.0
    private val paths = pathSequence.paths
    private var currentPathIdx = 0
    private var currentPathTraversal = PathTraversal(paths.first())
    val currentPosition: Point
        get() = currentPathTraversal.currentPosition
    val nonTraversedDistance: Double
        get() = currentPathTraversal.nonTraversedDistance

    fun traverse(distance: Float) {
        traverse(distance.toDouble())
    }

    fun traverse(distance: Double) {
        if (isTraversalFinished()) return
        var remainingDistanceToTraverse = distance

        while (remainingDistanceToTraverse > 0.0f) {
            currentPathTraversal.traverse(remainingDistanceToTraverse)
            distanceTraversed += remainingDistanceToTraverse - currentPathTraversal.nonTraversedDistance
            remainingDistanceToTraverse = currentPathTraversal.nonTraversedDistance

            if (currentPathTraversal.finishedTraversal()) {
                if (isLastPath()) return
                currentPathIdx++
                currentPathTraversal = PathTraversal(paths[currentPathIdx])
            }
        }
    }

    fun isTraversalFinished(): Boolean {
        return isLastPath() && currentPathTraversal.finishedTraversal()
    }

    private fun isLastPath(): Boolean {
        return currentPathIdx == (paths.size - 1)
    }
}