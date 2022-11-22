package com.xenotactic.korge.components

data class EntityTeleportInComponent(
    val sequenceNum: Int
) {
    val ordinalSequenceNumber get() = sequenceNum + 1
}