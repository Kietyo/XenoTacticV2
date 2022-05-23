package pathing

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PathSequenceTest {

    @Test
    fun pathLength() {
        assertEquals(1.0, PathSequence.create(Path.create(Point(0f, 0f), Point(0f, 1f))).pathLength)
        assertEquals(0.0, PathSequence(listOf()).pathLength)
    }
}