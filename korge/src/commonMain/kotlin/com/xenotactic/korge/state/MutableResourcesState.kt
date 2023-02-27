package com.xenotactic.korge.state

import com.xenotactic.gamelogic.engine.State

class MutableResourcesState(
    initialCurrentGold: Int,
): State {
    val initialMaxSupply: Int = 15
    var currentGold: Int = initialCurrentGold
        private set
    var currentSupply: Int = 0
        private set
}