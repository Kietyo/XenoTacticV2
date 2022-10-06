package com.xenotactic.korge.components

import com.xenotactic.ecs.EntityId

// Signifies that this entity is targeting another entity.
class TargetingComponent(
    val targetEntityId: EntityId
) {
}