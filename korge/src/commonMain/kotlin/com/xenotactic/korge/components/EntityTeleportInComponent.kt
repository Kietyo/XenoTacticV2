package com.xenotactic.korge.components

data class EntityTeleportInComponent(
    val sequenceNumber: Int
) {
    val ordinalSequenceNumber get() = sequenceNumber + 1
}