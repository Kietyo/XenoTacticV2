package com.xenotactic.gamelogic.components

data class EntityTeleportOutComponent(
    val sequenceNumber: Int
) {
    val ordinalSequenceNumber get() = sequenceNumber + 1
}
