package model

import com.xenotactic.gamelogic.containers.TypedEntityContainer
import com.xenotactic.gamelogic.model.MapEntity
import kotlin.test.Test
import kotlin.test.assertTrue

internal class TypedEntityContainerTest {

    @Test
    fun placeEntity() {
        val entityContainer = TypedEntityContainer<MapEntity.Checkpoint>()
        entityContainer.placeEntity(MapEntity.Checkpoint(0, 0, 0))
        assertTrue(entityContainer.containsEntity(0, 0))
        assertTrue(entityContainer.containsEntity(0, 1))
        assertTrue(entityContainer.containsEntity(1, 0))
        assertTrue(entityContainer.containsEntity(1, 1))

        assertTrue(!entityContainer.containsEntity(1, 2))
        assertTrue(!entityContainer.containsEntity(2, 1))
        assertTrue(!entityContainer.containsEntity(2, 2))

        assertTrue(!entityContainer.containsEntity(0, -1))
        assertTrue(!entityContainer.containsEntity(-1, 0))
        assertTrue(!entityContainer.containsEntity(-1, -1))
    }
}