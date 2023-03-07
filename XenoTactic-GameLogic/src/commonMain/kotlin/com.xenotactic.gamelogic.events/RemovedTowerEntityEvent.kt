package com.xenotactic.gamelogic.events

import com.xenotactic.ecs.EntityId

data class RemovedTowerEntityEvent(
    val entityId: EntityId
)