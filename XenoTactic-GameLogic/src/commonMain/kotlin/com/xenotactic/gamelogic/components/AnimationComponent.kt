package com.xenotactic.gamelogic.components

import com.xenotactic.ecs.IComponent
import kotlinx.serialization.Serializable

data class AnimationComponent(
    val baseChangeTimeMillis: Double,
    var cumulatedTimeMillisSinceLastFrame: Double
)