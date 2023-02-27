package com.xenotactic.gamelogic.pathing

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.toGameUnitPoint
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.lerp
import com.xenotactic.gamelogic.utils.toGameUnit

class SegmentTraversal(private val segment: Segment) {
    val currentDestinationPoint: GameUnitTuple get() = segment.point2
    var distanceTraversed = GameUnit(0.0)
    var currentPosition = segment.point1.copy()
        private set

    // This is only set once we've traversed passed the current segment length
    var nonTraversedDistance = GameUnit.ZERO
        private set

    fun traverse(distance: Double) {
        traverse(distance.toGameUnit())
    }

    fun traverse(distance: GameUnit) {
        if (finishedTraversal()) return
        val newDistanceTraversed = distanceTraversed + distance
        if (newDistanceTraversed < segment.length) {
            distanceTraversed = newDistanceTraversed
            currentPosition = segment.point1.toPoint().lerp(
                segment.point2.toPoint(), (newDistanceTraversed / segment.length).value
            ).toGameUnitPoint()
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
    var distanceTraversed = GameUnit.ZERO
        private set
    private val segments = path.getSegments()
    private var currentSegmentIdx = 0
    private var currentSegmentTraversal = SegmentTraversal(segments.first())
    val currentDestinationPoint: GameUnitTuple get() = currentSegmentTraversal.currentDestinationPoint
    val currentPosition: GameUnitTuple
        get() = currentSegmentTraversal.currentPosition

    val nonTraversedDistance: GameUnit
        get() = currentSegmentTraversal.nonTraversedDistance

    fun traverse(distance: Double) = traverse(distance.toGameUnit())

    /**
     * Traverses the path by the given distance.
     * - If we've already finish traversing the path then nothing is done.
     * - If the provided distance traverses past the path, then any non traversed distance will
     *   be stored in the `nonTraversedDistance` field.
     */
    fun traverse(distance: GameUnit) {
        if (finishedTraversal()) return
        var remainingDistanceToTraverse = distance

        while (remainingDistanceToTraverse > GameUnit.ZERO) {
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
    var distanceTraversed = GameUnit.ZERO
    private val paths = pathSequence.paths
    private var currentPathIdx = 0
    private var currentPathTraversal = PathTraversal(paths.first())
    val currentDestinationPoint: GameUnitTuple get() = currentPathTraversal.currentDestinationPoint
    val currentPosition: GameUnitTuple
        get() = currentPathTraversal.currentPosition
    val nonTraversedDistance: GameUnit
        get() = currentPathTraversal.nonTraversedDistance

    fun traverse(distance: Float) {
        traverse(distance.toGameUnit())
    }

    fun traverse(distance: GameUnit) {
        if (isTraversalFinished()) return
        var remainingDistanceToTraverse = distance

        while (remainingDistanceToTraverse > GameUnit.ZERO) {
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

