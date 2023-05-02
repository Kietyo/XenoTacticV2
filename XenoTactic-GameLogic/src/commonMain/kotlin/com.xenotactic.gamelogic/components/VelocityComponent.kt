package com.xenotactic.gamelogic.components

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlin.time.Duration

data class VelocityComponent(
    // GameUnits per second
    private val velocity: GameUnit = 5.toGameUnit()
) {
    fun calculateDistance(duration: Duration): GameUnit {
        val secs = duration.inWholeMicroseconds / 1e6
        return GameUnit(secs * velocity.value)
    }
}