package model

import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.Path
import com.xenotactic.gamelogic.pathing.PathSequence
import pathing.PathFinder

import test_utils.assertPathSequenceEquals
import kotlin.test.*

internal class GameMapTest {

    @Test
    fun getAllRocksAtPoint_emptyMap_noRocks() {
        val map = GameMap(10, 10)
        assertTrue(map.getAllRocksAtPoint(1, 1).count() == 0)
    }

    @Test
    fun getAllRocksAtPoint_oneRock_1x1() {
        val map = GameMap(10, 10)
        map.placeEntity(MapEntity.Rock(IntPoint(1, 1), 1, 1))
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 1, 1)),
            map.getAllRocksAtPoint(1, 1)
        )
        assertTrue(map.getAllRocksAtPoint(0, 0).count() == 0)
        assertTrue(map.getAllRocksAtPoint(0, 1).count() == 0)
        assertTrue(map.getAllRocksAtPoint(0, 2).count() == 0)
        assertTrue(map.getAllRocksAtPoint(1, 0).count() == 0)
        assertTrue(map.getAllRocksAtPoint(1, 2).count() == 0)
        assertTrue(map.getAllRocksAtPoint(2, 0).count() == 0)
        assertTrue(map.getAllRocksAtPoint(2, 1).count() == 0)
        assertTrue(map.getAllRocksAtPoint(2, 2).count() == 0)
    }

    @Test
    fun getAllRocksAtPoint_oneRock_4x2() {
        val map = GameMap(10, 10)
        map.placeEntity(MapEntity.Rock(IntPoint(1, 1), 4, 2))
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(1, 1)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(2, 1)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(3, 1)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(4, 1)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(1, 2)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(2, 1)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(3, 1)
        )
        assertContentEquals(
            sequenceOf(MapEntity.Rock(IntPoint(1, 1), 4, 2)),
            map.getAllRocksAtPoint(4, 1)
        )
    }

    @Test
    fun removeEntity_oneEntity() {
        val gameMap = GameMap(10, 10)
        gameMap.placeEntity(MapEntity.Rock(IntPoint(1, 1), 2, 2))

        assertContentEquals(listOf(MapEntity.Rock(IntPoint(1, 1), 2, 2)), gameMap.getAllEntities())

        gameMap.removeEntity(MapEntity.Rock(IntPoint(1, 1), 2, 2))

        assertTrue(gameMap.getAllEntities().isEmpty())
    }

    @Test
    fun removeEntity_twoOfSameEntity_removeOnlyOne() {
        val gameMap = GameMap(10, 10)
        gameMap.placeEntity(MapEntity.Rock(IntPoint(1, 1), 2, 2))
        gameMap.placeEntity(MapEntity.Rock(IntPoint(1, 1), 2, 2))

        assertContentEquals(
            listOf(
                MapEntity.Rock(IntPoint(1, 1), 2, 2),
                MapEntity.Rock(IntPoint(1, 1), 2, 2)
            ), gameMap.getAllEntities()
        )

        gameMap.removeEntity(MapEntity.Rock(IntPoint(1, 1), 2, 2))

        assertContentEquals(
            listOf(
                MapEntity.Rock(IntPoint(1, 1), 2, 2)
            ), gameMap.getAllEntities()
        )
    }

    @Test
    fun regressionTest1() {
        val gameMap = GameMap(10, 10)

        gameMap.placeEntities(
            MapEntity.Start(0, 0),
            MapEntity.Finish(7, 0),
            MapEntity.Checkpoint(0, 3, 6),
            MapEntity.Rock(2, 5, 4, 2)
        )

        assertPathSequenceEquals(
            PathSequence.create(
                Path.create(
                    Point(1.0, 1.0),
                    Point(1.9929289321881345, 7.007071067811865),
                    Point(4.0, 7.05)
                ),
                Path.create(
                    Point(4.0, 7.05),
                    Point(6.007071067811865, 7.007071067811865),
                    Point(8.0, 1.0)
                )
            ),
            PathFinder.getShortestPath(gameMap)!!
        )
    }


    @Test
    fun getEntitiesForType() {
        val gameMap = GameMap.create(
            100, 100,
            MapEntity.Start(0, 0),
            MapEntity.Finish(2, 2),
            MapEntity.CHECKPOINT.at(4, 4),
            MapEntity.ROCK_1X1.at(6, 6),
            MapEntity.Tower(8, 8),
            MapEntity.TELEPORT_IN.at(10, 10),
            MapEntity.TELEPORT_OUT.at(12, 12),
            MapEntity.SmallBlocker(14, 14)
        )

        assertContentEquals(
            sequenceOf(MapEntity.Start(0, 0)),
            gameMap.getEntitiesForType(MapEntityType.START)
        )

        assertContentEquals(
            sequenceOf(MapEntity.Finish(2, 2)),
            gameMap.getEntitiesForType(MapEntityType.FINISH)
        )

        assertContentEquals(
            sequenceOf(MapEntity.CHECKPOINT.at(4, 4)),
            gameMap.getEntitiesForType(MapEntityType.CHECKPOINT)
        )

        assertContentEquals(
            sequenceOf(MapEntity.ROCK_1X1.at(6, 6)),
            gameMap.getEntitiesForType(MapEntityType.ROCK)
        )

        assertContentEquals(
            sequenceOf(MapEntity.Tower(8, 8)),
            gameMap.getEntitiesForType(MapEntityType.TOWER)
        )

        assertContentEquals(
            sequenceOf(MapEntity.TELEPORT_IN.at(10, 10)),
            gameMap.getEntitiesForType(MapEntityType.TELEPORT_IN)
        )

        assertContentEquals(
            sequenceOf(MapEntity.TELEPORT_OUT.at(12, 12)),
            gameMap.getEntitiesForType(MapEntityType.TELEPORT_OUT)
        )

        assertContentEquals(
            sequenceOf(MapEntity.SmallBlocker(14, 14)),
            gameMap.getEntitiesForType(MapEntityType.SMALL_BLOCKER)
        )

        assertContentEquals(
            sequenceOf(
                MapEntity.Start(0, 0),
                MapEntity.Finish(2, 2),
                MapEntity.CHECKPOINT.at(4, 4),
                MapEntity.ROCK_1X1.at(6, 6),
                MapEntity.Tower(8, 8),
                MapEntity.TELEPORT_IN.at(10, 10),
                MapEntity.TELEPORT_OUT.at(12, 12),
                MapEntity.SmallBlocker(14, 14)
            ),
            gameMap.getEntitiesForTypes(
                MapEntityType.START,
                MapEntityType.FINISH,
                MapEntityType.CHECKPOINT,
                MapEntityType.ROCK,
                MapEntityType.TOWER,
                MapEntityType.TELEPORT_IN,
                MapEntityType.TELEPORT_OUT,
                MapEntityType.SMALL_BLOCKER,
            )
        )
    }


}