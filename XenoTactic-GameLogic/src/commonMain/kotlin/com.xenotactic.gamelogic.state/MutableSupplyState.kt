package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.engine.State

class MutableSupplyState: State {
    val initialMaxSupply: Int = 15
    var currentSupply: Int = 0
        private set
}