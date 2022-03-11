package test_utils

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity

val STANDARD_MAP = GameMap(10, 10).apply {
    placeEntities(
        MapEntity.Start(2, 0),
        MapEntity.Finish(4, 8),
        MapEntity.ROCK_4X2.at(2, 4),
        MapEntity.CHECKPOINT.at(8, 0),
        MapEntity.Tower(8, 4),
        MapEntity.TELEPORT_IN.at(6, 0),
        MapEntity.TELEPORT_OUT.at(8, 8)
    )
}

val STANDARD_MAP_START_AND_FINISH_ONLY = GameMap(10, 10).apply {
    placeEntities(
        MapEntity.Start(2, 0),
        MapEntity.ROCK_4X2.at(2, 4),
        MapEntity.Finish(4, 8),
    )
}