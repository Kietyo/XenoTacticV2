package com.xenotactic.gamelogic.utils

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