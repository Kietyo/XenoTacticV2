package pathing

import com.soywiz.korio.lang.assert
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import test_utils.assertDoubleEquals
import test_utils.assertPointEquals
import kotlin.test.Test
import kotlin.test.assertFalse

internal class PathTraversalTest {

    @Test
    fun traverse_pathWithOneSegment() {
        val path = Path.create(Point(0.0, 0.0), Point(0.0, 3.0))
        val traversal = PathTraversal(path)

        assertPointEquals(Point(0.0, 0.0), traversal.currentPosition)
        assertDoubleEquals(0.0, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(0.5)
        assertPointEquals(Point(0.0, 0.5), traversal.currentPosition)
        assertDoubleEquals(0.5, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(1.0)
        assertPointEquals(Point(0.0, 1.5), traversal.currentPosition)
        assertDoubleEquals(1.5, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(2.0)
        assertPointEquals(Point(0.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(3.0, traversal.distanceTraversed)
        assertDoubleEquals(path.pathLength, traversal.distanceTraversed)
        assertDoubleEquals(0.5, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())

        traversal.traverse(2.0)
        assertPointEquals(Point(0.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(3.0, traversal.distanceTraversed)
        assertDoubleEquals(0.5, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }

    @Test
    fun traverse_pathWithOneSegment_fullyTraversesInOneStep() {
        val path = Path.create(Point(0.0, 0.0), Point(0.0, 3.0))
        val traversal = PathTraversal(path)

        traversal.traverse(100.0)
        assertPointEquals(Point(0.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(3.0, traversal.distanceTraversed)
        assertDoubleEquals(97.0, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }


    @Test
    fun traverse_pathWithTwoSegments() {
        val path = Path.create(Point(0.0, 0.0), Point(0.0, 3.0), Point(5.0, 3.0))
        val traversal = PathTraversal(path)

        assertPointEquals(Point(0.0, 0.0), traversal.currentPosition)
        assertDoubleEquals(0.0, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(0.5)
        assertPointEquals(Point(0.0, 0.5), traversal.currentPosition)
        assertDoubleEquals(0.5, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(1.0)
        assertPointEquals(Point(0.0, 1.5), traversal.currentPosition)
        assertDoubleEquals(1.5, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(2.0)
        assertPointEquals(Point(0.5, 3.0), traversal.currentPosition)
        assertDoubleEquals(3.5, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(0.5)
        assertPointEquals(Point(1.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(4.0, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(3.0)
        assertPointEquals(Point(4.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(7.0, traversal.distanceTraversed)
        assertDoubleEquals(0.0, traversal.nonTraversedDistance)
        assertFalse(traversal.finishedTraversal())

        traversal.traverse(3.0)
        assertPointEquals(Point(5.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(8.0, traversal.distanceTraversed)
        assertDoubleEquals(path.pathLength, traversal.distanceTraversed)
        assertDoubleEquals(2.0, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }

    @Test
    fun traverse_pathWithTwoSegments_fullyTraversesInOneStep() {
        val path = Path.create(Point(0.0, 0.0), Point(0.0, 3.0), Point(5.0, 3.0))
        val traversal = PathTraversal(path)

        traversal.traverse(100.0)
        assertPointEquals(Point(5.0, 3.0), traversal.currentPosition)
        assertDoubleEquals(8.0, traversal.distanceTraversed)
        assertDoubleEquals(92.0, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }

    @Test
    fun traverse_pathWith100Segments() {
        val vectorList = mutableListOf<Point>()
        for (i in 0..100) {
            vectorList.add(Point(i.toDouble(), 0.0))
        }

        val path = Path(vectorList.toList())
        val traversal = PathTraversal(path)

        traversal.traverse(250.0)
        assertPointEquals(Point(100.0, 0.0), traversal.currentPosition)
        assertDoubleEquals(100.0, traversal.distanceTraversed)
        assertDoubleEquals(150.0, traversal.nonTraversedDistance)
        assert(traversal.finishedTraversal())
    }
}