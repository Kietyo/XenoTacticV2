package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.utils.GameEvent
import com.xenotactic.gamelogic.utils.State

class MutableEventQueueState(
    private val eventQueue: MutableList<GameEvent> = mutableListOf<GameEvent>()
) : State {
    val isNotEmpty get() = eventQueue.isNotEmpty()
    val isEmpty get() = eventQueue.isEmpty()

    fun add(event: GameEvent) {
        eventQueue.add(event)
    }
    fun toList() = eventQueue.toList()
    fun clearEvents() = eventQueue.clear()
}