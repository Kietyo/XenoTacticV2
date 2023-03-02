package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.engine.State

class MutableGoldState(
    initialCurrentGold: Int,
): State {
    var currentGold: Int = initialCurrentGold
        private set
}