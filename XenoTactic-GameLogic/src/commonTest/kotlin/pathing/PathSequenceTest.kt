package pathing

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.testing.assertThat
import kotlin.test.Test

internal class PathSequenceTest {

    @Test
    fun pathLength() {
        assertThat(PathSequence.create(Path.create(Point(0f, 0f), Point(0f, 1f))).pathLength).almostEqualsTo(1.0)
        assertThat(PathSequence(listOf()).pathLength).almostEqualsTo(0.0)
    }
}