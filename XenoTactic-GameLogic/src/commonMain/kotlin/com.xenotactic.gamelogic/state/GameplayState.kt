package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.utils.State

data class GameplayState(val maxSpeedUpgrades: Int, // E.g 0.04 = 4% speed increase
    val speedPercentPerUpgrade: Double, val basicTowerCost: Int, val initialDamageUpgradeCost: Int,
    val initialSpeedUpgradeCost: Int, val supplyDepotCost: Int) : State {
    val initialMaxSupply = 15
    val supplyPerDepot = 15

    companion object {
        val DEFAULT = GameplayState(3, 0.04, 7, 4, 5, 31)
    }
}