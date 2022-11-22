package com.xenotactic.korge.components

data class EntitySpeedAreaComponent(
    val speedEffect: Double
) {
    val speedText get() = "${(speedEffect * 100).toInt()}%"
}