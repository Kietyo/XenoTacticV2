package model


import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MapEntityTest {
//    @Test
////    @Ignore
//    fun intersectsUnitBlock_profile() {
//        val rock = MapEntity.Rock(1,1, 2, 2)
//        var timeA = 0L
//        var timeB = 0L
//        repeat (1000000) {
//            for (i in 0..4) {
//                for (j in 0..4) {
////                    run {
////                        val resultA = measureTime(printMessage = false) {
////                            rock.intersectsUnitBlock(i, j)
////                        }
////                        val resultB = measureTime(printMessage = false) {
////                            rock.intersectsUnitBlock2(i, j)
////                        }
////                        assertEquals(resultA.second, resultB.second)
////                        timeA += resultA.first
////                        timeB += resultB.first
////                    }
//                    run {
//                        val resultB = measureTime(printMessage = false) {
//                            rock.intersectsUnitBlock2(i, j)
////                            false
//                        }
//                        val resultA = measureTime(printMessage = false) {
//                            rock.intersectsUnitBlock(i, j)
//                        }
//                        assertEquals(resultA.second, resultB.second)
//                        timeA += resultA.first
//                        timeB += resultB.first
//                    }
//                }
//            }
//        }
//        println("""
//            timeA: $timeA,
//            timeB: $timeB
//        """.trimIndent())
//    }

    @Test
    fun unitSquarePoints() {
        val entity = MapEntity.Rock(1, 1, 1, 1)

        assertEquals(
            GameUnitTuple(1, 1),
            entity.topLeftUnitSquareGameUnitPoint
        )
        assertEquals(
            GameUnitTuple(1, 1),
            entity.topRightUnitSquareGameUnitPoint
        )
        assertEquals(
            GameUnitTuple(1, 1),
            entity.bottomLeftUnitSquareGameUnitPoint
        )
        assertEquals(
            GameUnitTuple(1, 1),
            entity.bottomRightUnitSquareGameUnitPoint
        )
    }

    @Test
    fun intersectsBlock() {
        val entity = MapEntity.Rock(1, 1, 1, 1)
        assertFalse(entity.intersectsUnitBlock(0, 0))
        assertFalse(entity.intersectsUnitBlock(1, 0))
        assertFalse(entity.intersectsUnitBlock(1, 2))
        assertFalse(entity.intersectsUnitBlock(2, 0))
        assertFalse(entity.intersectsUnitBlock(2, 1))
        assertFalse(entity.intersectsUnitBlock(2, 2))
        assertFalse(entity.intersectsUnitBlock(0, 1))
        assertFalse(entity.intersectsUnitBlock(0, 2))
        assertTrue(entity.intersectsUnitBlock(1, 1))
    }

    @Test
    fun intersectsBlock2() {
        val entity = MapEntity.Rock(1, 1, 2, 2)
        assertFalse(entity.intersectsUnitBlock(0, 0))
        assertFalse(entity.intersectsUnitBlock(0, 1))
        assertFalse(entity.intersectsUnitBlock(0, 2))
        assertFalse(entity.intersectsUnitBlock(0, 3))

        assertFalse(entity.intersectsUnitBlock(3, 0))
        assertFalse(entity.intersectsUnitBlock(3, 1))
        assertFalse(entity.intersectsUnitBlock(3, 2))
        assertFalse(entity.intersectsUnitBlock(3, 3))

        assertFalse(entity.intersectsUnitBlock(1, 0))
        assertFalse(entity.intersectsUnitBlock(2, 0))

        assertFalse(entity.intersectsUnitBlock(1, 3))
        assertFalse(entity.intersectsUnitBlock(2, 3))

        assertTrue(entity.intersectsUnitBlock(1, 1))
        assertTrue(entity.intersectsUnitBlock(1, 2))
        assertTrue(entity.intersectsUnitBlock(2, 1))
        assertTrue(entity.intersectsUnitBlock(2, 2))
    }

    @Test
    fun intersectsEntity() {
        val entity = MapEntity.Rock(1, 1, 2, 2)
        assertFalse(entity.intersectsEntity(MapEntity.Rock(0, 0, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(0, 1, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(0, 2, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(0, 3, 1, 1)))

        assertFalse(entity.intersectsEntity(MapEntity.Rock(3, 0, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(3, 1, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(3, 2, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(3, 3, 1, 1)))

        assertFalse(entity.intersectsEntity(MapEntity.Rock(1, 0, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(2, 0, 1, 1)))

        assertFalse(entity.intersectsEntity(MapEntity.Rock(1, 3, 1, 1)))
        assertFalse(entity.intersectsEntity(MapEntity.Rock(2, 3, 1, 1)))

        assertTrue(entity.intersectsEntity(MapEntity.Rock(1, 1, 1, 1)))
        assertTrue(entity.intersectsEntity(MapEntity.Rock(1, 2, 1, 1)))
        assertTrue(entity.intersectsEntity(MapEntity.Rock(2, 1, 1, 1)))
        assertTrue(entity.intersectsEntity(MapEntity.Rock(2, 2, 1, 1)))
        assertTrue(entity.intersectsEntity(MapEntity.Rock(1, 1, 2, 2)))
    }

    @Test
    fun intersectsEntity2() {
        val entity1 = MapEntity.Rock(1, 1, 2, 2)
        val entity2 = MapEntity.Rock(2, 2, 2, 2)
        assertTrue(entity1.intersectsEntity(entity2))
        assertTrue(entity2.intersectsEntity(entity1))
    }

    @Test
    fun intersectsEntity3() {
        val entity1 = MapEntity.Rock(7, 7, 2, 2)
        val entity2 = MapEntity.Rock(8, 7, 2, 2)
        assertTrue(entity1.intersectsEntity(entity2))
        assertTrue(entity2.intersectsEntity(entity1))
    }

    @Test
    fun intersectsEntity4() {
        val entity1 = MapEntity.Rock(3, 7, 2, 2)
        val entity2 = MapEntity.Rock(3, 8, 2, 2)
        assertTrue(entity1.intersectsEntity(entity2))
        assertTrue(entity2.intersectsEntity(entity1))
    }

    @Test
    fun intersectsEntity5() {
        val entity1 = MapEntity.Start(0, 0)
        val entity2 = MapEntity.TELEPORT_IN.at(1, 0)
        assertTrue(entity1.intersectsEntity(entity2))
        assertTrue(entity2.intersectsEntity(entity1))
    }

    @Test
    fun fullyCovers() {
        assertFalse(
            MapEntity.fullyCovers(
                MapEntity.Rock(3, 7, 2, 2),
                MapEntity.Rock(3, 8, 2, 2)
            )
        )
        assertTrue(
            MapEntity.fullyCovers(
                MapEntity.Rock(3, 7, 2, 2),
                MapEntity.Rock(3, 7, 2, 2)
            )
        )
        assertFalse(
            MapEntity.fullyCovers(
                MapEntity.Rock(1, 1, 4, 2),
                MapEntity.Rock(2, 1, 2, 2)
            )
        )
    }

    @Test
    fun isFullyCoveredBy() {
        assertFalse {
            MapEntity.TELEPORT_IN.at(1, 10).isFullyCoveredBy(
                MapEntity.ROCK_2X4.at(1, 11)
            )
        }

        assertTrue {
            MapEntity.TELEPORT_IN.at(1, 10).isFullyCoveredBy(
                listOf(
                    MapEntity.ROCK_2X4.at(1, 11),
                    MapEntity.ROCK_4X2.at(0, 10),
                )
            )
        }

        assertTrue {
            MapEntity.TELEPORT_IN.at(1, 10).isFullyCoveredBy(
                listOf(
                    MapEntity.ROCK_2X4.at(0, 8),
                    MapEntity.ROCK_2X4.at(1, 11),
                    MapEntity.ROCK_4X2.at(0, 10),
                )
            )
        }
    }
}