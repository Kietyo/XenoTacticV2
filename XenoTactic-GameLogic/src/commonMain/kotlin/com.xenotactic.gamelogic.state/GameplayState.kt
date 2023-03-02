package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.engine.State

data class GameplayState(
    val maxSpeedUpgrades: Int,
    // E.g 0.04 = 4% speed increase
    val speedPercentPerUpgrade: Double,
    val basicTowerCost: Int
): State {
    companion object {
        val DEFAULT = GameplayState(3, 0.04, 7)
    }
}