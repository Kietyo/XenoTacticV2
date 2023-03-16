package com.xenotactic.gamelogic.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    var userName: String = "XenoTactic",
    var maps: MutableMap<String, GameMap> = mutableMapOf()
)
