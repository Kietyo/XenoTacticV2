package com.xenotactic.gamelogic.events

import com.xenotactic.ecs.EntityId

data class AddedEntityEvent(
    val entityId: EntityId
)