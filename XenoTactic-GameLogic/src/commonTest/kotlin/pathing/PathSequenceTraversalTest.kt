package pathing

import korlibs.io.lang.assert
import com.xenotactic.gamelogic.model.IPoint


import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.pathing.PathSequenceTraversal
import com.xenotactic.gamelogic.utils.assertDoubleEquals
import com.xenotactic.gamelogic.utils.assertFloatEquals
import com.xenotactic.gamelogic.utils.assertPointEquals
import kotlin.test.Test
import kotlin.test.assertFalse

internal class PathSequenceTraversalTest {

    @Test
    fun traverse_onePath() {
        val path = Path.create(IPoint(0.0f, 0f), IPoint(0.0f, 3f))
        val traversal = PathSequenceTraversal(
            PathSequence.create(path)
        )

        assertPointEquals(IPoint(0.0f, 0f), traversal.currentPosition)
        assertFloatEquals(0.0f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(0.5f)
        assertPointEquals(IPoint(0.0f, 0.5f), traversal.currentPosition)
        assertFloatEquals(0.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(1f)
        assertPointEquals(IPoint(0.0f, 1.5f), traversal.currentPosition)
        assertFloatEquals(1.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(2f)
        assertPointEquals(IPoint(0.0f, 3f), traversal.currentPosition)
        assertFloatEquals(3f, traversal.distanceTraversed)
        assertDoubleEquals(path.pathLength, traversal.distanceTraversed)
        assertFloatEquals(0.5f, traversal.nonTraversedDistance)
        assert(traversal.isTraversalFinished())

        traversal.traverse(2f)
        assertPointEquals(IPoint(0.0f, 3f), traversal.currentPosition)
        assertFloatEquals(3f, traversal.distanceTraversed)
        assertFloatEquals(0.5f, traversal.nonTraversedDistance)
        assert(traversal.isTraversalFinished())
    }

    @Test
    fun traverse_twoPaths() {
        val pathSequence = PathSequence.create(
            Path.create(IPoint(0.0f, 0f), IPoint(0.0f, 3f)),
            Path.create(IPoint(0.0f, 3f), IPoint(5f, 3f))
        )
        val traversal = PathSequenceTraversal(
            pathSequence
        )

        assertPointEquals(IPoint(0.0f, 0f), traversal.currentPosition)
        assertFloatEquals(0.0f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(0.5f)
        assertPointEquals(IPoint(0.0f, 0.5f), traversal.currentPosition)
        assertFloatEquals(0.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(1f)
        assertPointEquals(IPoint(0.0f, 1.5f), traversal.currentPosition)
        assertFloatEquals(1.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(2f)
        assertPointEquals(IPoint(0.5f, 3f), traversal.currentPosition)
        assertFloatEquals(3.5f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(0.5f)
        assertPointEquals(IPoint(1f, 3f), traversal.currentPosition)
        assertFloatEquals(4f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(3f)
        assertPointEquals(IPoint(4f, 3f), traversal.currentPosition)
        assertFloatEquals(7f, traversal.distanceTraversed)
        assertFloatEquals(0.0f, traversal.nonTraversedDistance)
        assertFalse(traversal.isTraversalFinished())

        traversal.traverse(3f)
        assertPointEquals(IPoint(5f, 3f), traversal.currentPosition)
        assertFloatEquals(8f, traversal.distanceTraversed)
        assertDoubleEquals(pathSequence.pathLength, traversal.distanceTraversed)
        assertFloatEquals(2f, traversal.nonTraversedDistance)
        assert(traversal.isTraversalFinished())
    }

    @Test
    fun traverse_twoPaths_traversesWholePathInOneStep() {
        val pathSequence = PathSequence.create(
            Path.create(IPoint(0.0f, 0f), IPoint(0.0f, 3f)),
            Path.create(IPoint(0.0f, 3f), IPoint(5f, 3f))
        )
        val traversal = PathSequenceTraversal(
            pathSequence
        )

        traversal.traverse(100f)
        assertPointEquals(IPoint(5f, 3f), traversal.currentPosition)
        assertFloatEquals(8f, traversal.distanceTraversed)
        assertDoubleEquals(pathSequence.pathLength, traversal.distanceTraversed)
        assertFloatEquals(92f, traversal.nonTraversedDistance)
        assert(traversal.isTraversalFinished())
    }
}