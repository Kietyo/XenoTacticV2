package com.xenotactic.gamelogic.components

data class EntityTeleportInComponent(
    val sequenceNumber: Int
) {
    val ordinalSequenceNumber get() = sequenceNumber + 1
}