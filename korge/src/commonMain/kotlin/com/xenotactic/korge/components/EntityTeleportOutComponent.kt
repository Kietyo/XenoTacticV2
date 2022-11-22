package com.xenotactic.korge.components

data class EntityTeleportOutComponent(
    val sequenceNum: Int
) {
    val ordinalSequenceNumber get() = sequenceNum + 1
}
