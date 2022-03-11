package utils

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.utils.EntitiesBlockingEntityResult
import com.xenotactic.gamelogic.utils.EntitiesBlockingEntityUtil
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EntitiesBlockingEntityUtilTest {

    @Test
    fun topSideBlocking() {
        assertEquals(
            EntitiesBlockingEntityResult(
                topLeftDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 4)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                topPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(2, 4)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                topPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(3, 4)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                topRightDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 4)
                )
            )
        )
    }

    @Test
    fun bottomSideBlocking() {
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomLeftDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 1)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(2, 1)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(3, 1)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomRightDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 1)
                )
            )
        )
    }

    @Test
    fun leftSideBlocking() {
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomLeftDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 1)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                leftPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 2)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                leftPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 3)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                topLeftDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 4)
                )
            )
        )
    }

    @Test
    fun rightSideBlocking() {
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomRightDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 1)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                rightPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 2)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                rightPartiallyBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 3)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                topRightDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 4)
                )
            )
        )
    }

    @Test
    fun diagonalsWork() {
        assertEquals(
            EntitiesBlockingEntityResult(
                topRightDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 4)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                topLeftDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 4)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomLeftDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(1, 1)
                )
            )
        )
        assertEquals(
            EntitiesBlockingEntityResult(
                bottomRightDiagonalBlocking = true
            ),
            EntitiesBlockingEntityUtil(
                MapEntity.Tower(2, 2),
                GameMap.create(
                    10, 10,
                    MapEntity.ROCK_1X1.at(4, 1)
                )
            )
        )
    }
}