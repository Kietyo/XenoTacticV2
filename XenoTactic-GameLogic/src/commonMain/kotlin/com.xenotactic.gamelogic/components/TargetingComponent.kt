package com.xenotactic.gamelogic.components

import com.xenotactic.ecs.EntityId

// Signifies that this entity is targeting another entity.
class TargetingComponent(
    val targetEntityId: EntityId
) {
}