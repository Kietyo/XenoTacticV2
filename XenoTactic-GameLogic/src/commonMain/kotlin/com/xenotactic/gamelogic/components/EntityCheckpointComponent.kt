package com.xenotactic.gamelogic.components

data class EntityCheckpointComponent(
    // Starts at 0
    val sequenceNumber: Int
) {
    val ordinalSequenceNumber get() = sequenceNumber + 1
}