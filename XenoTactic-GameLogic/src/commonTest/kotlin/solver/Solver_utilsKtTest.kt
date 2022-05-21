package solver

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.to
import kotlin.test.Test
import kotlin.test.assertEquals


internal class Solver_utilsKtTest {

    @Test
    fun getAvailableTowerPlacementSpots_2x2_noEntities() {
        val map = GameMap(2 ,2)

        assertEquals(
            listOf(IntPoint(0, 0)),
            solver.getAvailableTowerPlacementSpots(map)
        )
    }

    @Test
    fun getAvailableTowerPlacementSpots_5x5() {
        val map = GameMap(5, 5)
        map.placeEntity(MapEntity.Rock(2, 2, 1, 1))

        assertEquals(
            listOf(
                0 to 0,
                0 to 1,
                0 to 2,
                0 to 3,
                1 to 0,
                1 to 3,
                2 to 0,
                2 to 3,
                3 to 0,
                3 to 1,
                3 to 2,
                3 to 3
            ),
            solver.getAvailableTowerPlacementSpots(map)
        )

        map.placeEntity(MapEntity.Rock(0, 0, 2, 2))
        assertEquals(
            listOf(
                0 to 2,
                0 to 3,
                1 to 3,
                2 to 0,
                2 to 3,
                3 to 0,
                3 to 1,
                3 to 2,
                3 to 3
            ),
            solver.getAvailableTowerPlacementSpots(map)
        )
    }

    @Test
    fun allEntitiesTakenIntoConsideration() {
        for (type in MapEntityType.values()) {
            when (type) {
                MapEntityType.START -> {
                    val map = GameMap(2 ,2)
                    map.placeEntity(MapEntity.Start(0, 0))

                    assertEquals(
                        emptyList<IntPoint>(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.FINISH -> {
                    val map = GameMap(2, 2)
                    map.placeEntity(MapEntity.Finish(0, 0))

                    assertEquals(
                        emptyList<IntPoint>(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.CHECKPOINT -> {
                    val map = GameMap(2, 2)
                    map.placeEntity(MapEntity.CHECKPOINT.at(0, 0))

                    assertEquals(
                        emptyList<IntPoint>(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.ROCK -> {
                    val map = GameMap(2, 2)
                    map.placeEntity(MapEntity.Rock(0, 0, 2, 2))

                    assertEquals(
                        emptyList<IntPoint>(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.TOWER -> {
                    val map = GameMap(2 ,2)
                    map.placeEntity(MapEntity.Tower(0, 0))

                    assertEquals(
                        emptyList<IntPoint>(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.TELEPORT_IN -> {
                    val map = GameMap(2 ,2)
                    map.placeEntity(MapEntity.TELEPORT_IN.at(0, 0))

                    assertEquals(
                        listOf(IntPoint(0, 0)),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.TELEPORT_OUT -> {
                    val map = GameMap(2 ,2)
                    map.placeEntity(MapEntity.TELEPORT_OUT.at(0, 0))

                    assertEquals(
                        emptyList<IntPoint>(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.SMALL_BLOCKER -> {
                    val map = GameMap(2, 2)
                    map.placeEntity(MapEntity.SmallBlocker(0, 0))

                    assertEquals(
                        emptyList(),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
                MapEntityType.SPEED_AREA -> {
                    val map = GameMap(2, 2)
                    map.placeEntity(MapEntity.SpeedArea(1, 1, 1, 0.5))

                    assertEquals(
                        listOf(IntPoint(0, 0)),
                        getAvailableTowerPlacementSpots(map)
                    )
                }
            }
        }
    }
}