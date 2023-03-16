package com.xenotactic.gamelogic.components

data class AnimationComponent(
    val baseChangeTimeMillis: Double,
    var cumulatedTimeMillisSinceLastFrame: Double
)