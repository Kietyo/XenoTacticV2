package com.xenotactic.korge.components

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

data class MovementSpeedComponent(
    val movementSpeed: GameUnit = 0.1.toGameUnit()
)