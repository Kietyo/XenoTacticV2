package com.xenotactic.gamelogic.utils

import com.xenotactic.gamelogic.api.GameEvent

data class LogEntry(
    val tickNum: Long,
    val events: List<GameEvent>
)

data class EventLog(
    private val events: MutableList<LogEntry> = mutableListOf<LogEntry>()
) {
    fun recordEntry(logEntry: LogEntry) {
        events.add(logEntry)
    }
}