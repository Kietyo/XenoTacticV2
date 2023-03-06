package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.api.GameEvent
import com.xenotactic.gamelogic.engine.State

class MutableEventQueueState(
    private val eventQueue: MutableList<GameEvent> = mutableListOf<GameEvent>()
) : State {
    fun add(event: GameEvent) {
        eventQueue.add(event)
    }
    fun toList() = eventQueue.toList()
    fun clearEvents() = eventQueue.clear()
}