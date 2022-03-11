package model

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BlockingIntPointContainerTest {

    @Test
    fun test() {
        val blockingPointContainer = BlockingPointContainer.Mutable()
        assertEquals(0, blockingPointContainer.size)

        blockingPointContainer.add(1, 1)
        assertEquals(1, blockingPointContainer.size)
        assertTrue(blockingPointContainer.contains(1, 1))

        blockingPointContainer.add(1, 1)
        assertEquals(2, blockingPointContainer.size)
        assertTrue(blockingPointContainer.contains(1, 1))

        blockingPointContainer.remove(1, 1)
        assertEquals(1, blockingPointContainer.size)
        assertTrue(blockingPointContainer.contains(1, 1))

        blockingPointContainer.remove(1, 1)
        assertEquals(0, blockingPointContainer.size)
        assertFalse(blockingPointContainer.contains(1, 1))

        blockingPointContainer.remove(1, 1)
        assertEquals(0, blockingPointContainer.size)
        assertFalse(blockingPointContainer.contains(1, 1))

        blockingPointContainer.add(1, 1)
        assertEquals(1, blockingPointContainer.size)
        assertTrue(blockingPointContainer.contains(1, 1))

        blockingPointContainer.add(1, 1)
        assertEquals(2, blockingPointContainer.size)
        assertTrue(blockingPointContainer.contains(1, 1))

        blockingPointContainer.remove(1, 1)
        assertEquals(1, blockingPointContainer.size)
        assertTrue(blockingPointContainer.contains(1, 1))

        blockingPointContainer.remove(1, 1)
        assertEquals(0, blockingPointContainer.size)
        assertFalse(blockingPointContainer.contains(1, 1))

        blockingPointContainer.remove(2, 2)
        blockingPointContainer.remove(3, 3)
        blockingPointContainer.remove(4, 4)
        assertFalse(blockingPointContainer.contains(2, 2))
        assertFalse(blockingPointContainer.contains(3, 3))
        assertFalse(blockingPointContainer.contains(4, 4))
        assertEquals(0, blockingPointContainer.size)
    }
}