package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    var userName: String = "XenoTactic",
    var maps: MutableList<GameMap> = mutableListOf()
)
