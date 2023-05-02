package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.utils.State
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.GoldStateUpdated

class MutableGoldState(
    initialCurrentGold: Int,
    val eventBus: EventBus? = null
): State {
    var currentGold: Int = initialCurrentGold
        set(value) {
            val prev = field
            field = value
            eventBus?.send(GoldStateUpdated(prev, value))
        }
}