import com.xenotactic.gamelogic.model.GameUnitTuple
import kotlin.test.Test
import kotlin.test.assertTrue

internal class AdhocTest {

    @Test
    fun mapGet() {
        val map = mutableMapOf<Set<GameUnitTuple>, String>()

        val set1 = setOf(GameUnitTuple(2, 2), GameUnitTuple(3, 3))
        map[set1] = "blah"

        assertTrue(map.containsKey(set1))

        val set2 = setOf(GameUnitTuple(3, 3), GameUnitTuple(2, 2))
        assertTrue(map.containsKey(set2))
    }
}