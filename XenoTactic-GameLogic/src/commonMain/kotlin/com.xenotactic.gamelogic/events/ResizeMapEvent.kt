package com.xenotactic.gamelogic.events

import com.xenotactic.gamelogic.utils.GameUnit

data class ResizeMapEvent(
    val oldMapWidth: GameUnit,
    val oldMapHeight: GameUnit,
    val newMapWidth: GameUnit,
    val newMapHeight: GameUnit
)