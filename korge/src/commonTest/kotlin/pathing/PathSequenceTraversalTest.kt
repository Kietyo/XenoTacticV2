package pathing

import com.soywiz.korio.lang.assert
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import test_utils.assertDoubleEquals
import test_utils.assertFloatEquals
import test_utils.assertPointEquals
import kotlin.test.Test
import kotlin.test.assertFalse

internal class PathSequenceTraversalTest {

    @Test
    fun traverse_onePath() {
        val path = Path.create(Point(0.0f, 0f), Point(0.0f, 3f))
        val traversal = PathSequenceTraversal(
            PathSequence.create(path)
        )

        assertPointEquals(Point(0.0f, 0f), traversal.currentPosition)
        assertFloatEquals(0.0f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(0.5f)
        assertPointEquals(Point(0.0f, 0.5f), traversal.currentPosition)
        assertFloatEquals(0.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(1f)
        assertPointEquals(Point(0.0f, 1.5f), traversal.currentPosition)
        assertFloatEquals(1.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(2f)
        assertPointEquals(Point(0.0f, 3f), traversal.currentPosition)
        assertFloatEquals(3f, traversal.distanceTraversed)
        assertDoubleEquals(path.pathLength, traversal.distanceTraversed)
        assertFloatEquals(0.5f, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())

        traversal.traverse(2f)
        assertPointEquals(Point(0.0f, 3f), traversal.currentPosition)
        assertFloatEquals(3f, traversal.distanceTraversed)
        assertFloatEquals(0.5f, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }

    @Test
    fun traverse_twoPaths() {
        val pathSequence = PathSequence.create(
            Path.create(Point(0.0f, 0f), Point(0.0f, 3f)),
            Path.create(Point(0.0f, 3f), Point(5f, 3f))
        )
        val traversal = PathSequenceTraversal(
            pathSequence
        )

        assertPointEquals(Point(0.0f, 0f), traversal.currentPosition)
        assertFloatEquals(0.0f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(0.5f)
        assertPointEquals(Point(0.0f, 0.5f), traversal.currentPosition)
        assertFloatEquals(0.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(1f)
        assertPointEquals(Point(0.0f, 1.5f), traversal.currentPosition)
        assertFloatEquals(1.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(2f)
        assertPointEquals(Point(0.5f, 3f), traversal.currentPosition)
        assertFloatEquals(3.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(0.5f)
        assertPointEquals(Point(1f, 3f), traversal.currentPosition)
        assertFloatEquals(4f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(3f)
        assertPointEquals(Point(4f, 3f), traversal.currentPosition)
        assertFloatEquals(7f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(3f)
        assertPointEquals(Point(5f, 3f), traversal.currentPosition)
        assertFloatEquals(8f, traversal.distanceTraversed)
        assertDoubleEquals(pathSequence.pathLength, traversal.distanceTraversed)
        assertFloatEquals(2f, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }

    @Test
    fun traverse_twoPaths_traversesWholePathInOneStep() {
        val pathSequence = PathSequence.create(
            Path.create(Point(0.0f, 0f), Point(0.0f, 3f)),
            Path.create(Point(0.0f, 3f), Point(5f, 3f))
        )
        val traversal = PathSequenceTraversal(
            pathSequence
        )

        traversal.traverse(100f)
        assertPointEquals(Point(5f, 3f), traversal.currentPosition)
        assertFloatEquals(8f, traversal.distanceTraversed)
        assertDoubleEquals(pathSequence.pathLength, traversal.distanceTraversed)
        assertFloatEquals(92f, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }
}