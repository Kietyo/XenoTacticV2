package com.xenotactic.gamelogic.components

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

data class VelocityComponent(
    // GameUnits per tick
    val velocity: GameUnit = 0.1.toGameUnit()
)