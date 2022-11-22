package com.xenotactic.korge.components

data class EntityCheckpointComponent(
    // Starts at 0
    val sequenceNum: Int
) {
    val ordinalSequenceNumber get() = sequenceNum + 1
}