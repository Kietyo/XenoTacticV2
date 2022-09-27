import com.xenotactic.gamelogic.model.GameUnitPoint
import kotlin.test.Test
import kotlin.test.assertTrue

internal class AdhocTest {

    @Test
    fun mapGet() {
        val map = mutableMapOf<Set<GameUnitPoint>, String>()

        val set1 = setOf(GameUnitPoint(2, 2), GameUnitPoint(3, 3))
        map[set1] = "blah"

        assertTrue(map.containsKey(set1))

        val set2 = setOf(GameUnitPoint(3, 3), GameUnitPoint(2, 2))
        assertTrue(map.containsKey(set2))
    }
}