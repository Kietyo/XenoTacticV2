package com.xenotactic.korge.state

import com.xenotactic.korge.engine.State

data class GameplayState(
    val maxSpeedUpgrades: Int,
    // E.g 0.04 = 4% speed increase
    val speedPercentPerUpgrade: Double
): State