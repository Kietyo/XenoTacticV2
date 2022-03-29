package com.xenotactic.gamelogic.firebase_models

import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.gamelogic.model.GameMap

data class FbMapEntry(
    val id: String,
    val map: GameMap
) {
    operator fun invoke(map: GameMap) = FbMapEntry(
        MapToId.calculateId(map),
        map
    )
}