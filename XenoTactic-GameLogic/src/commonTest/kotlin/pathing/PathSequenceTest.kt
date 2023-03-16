package pathing



import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.testing.assertThat
import kotlin.test.Test

internal class PathSequenceTest {

    @Test
    fun pathLength() {
        assertThat(PathSequence.create(Path.create(IPoint(0f, 0f), IPoint(0f, 1f))).pathLength).isEqualTo(1.0)
        assertThat(PathSequence(listOf()).pathLength).isEqualTo(0.0)
    }
}