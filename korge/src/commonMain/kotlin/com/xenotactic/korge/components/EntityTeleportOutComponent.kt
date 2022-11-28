package com.xenotactic.korge.components

data class EntityTeleportOutComponent(
    val sequenceNumber: Int
) {
    val ordinalSequenceNumber get() = sequenceNumber + 1
}
