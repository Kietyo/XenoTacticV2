package com.xenotactic.korge.components

data class AnimationComponent(
    val baseChangeTimeMillis: Double,
    var cumulatedTimeMillisSinceLastFrame: Double
) {
}