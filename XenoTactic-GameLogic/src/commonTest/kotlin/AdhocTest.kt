import com.xenotactic.gamelogic.model.IntPoint
import kotlin.test.Test
import kotlin.test.assertTrue

internal class AdhocTest {

    @Test
    fun mapGet() {
        val map = mutableMapOf<Set<IntPoint>, String>()

        val set1 = setOf(IntPoint(2, 2), IntPoint(3, 3))
        map[set1] = "blah"

        assertTrue(map.containsKey(set1))

        val set2 = setOf(IntPoint(3, 3), IntPoint(2, 2))
        assertTrue(map.containsKey(set2))
    }
}