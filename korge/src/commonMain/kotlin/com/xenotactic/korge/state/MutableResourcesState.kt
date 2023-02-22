package com.xenotactic.korge.state

import com.xenotactic.korge.engine.State

class MutableResourcesState(
    initialCurrentGold: Int,
): State {
    val initialMaxSupply: Int = 15
    var currentGold: Int = initialCurrentGold
        private set
    var currentSupply: Int = 0
        private set
}