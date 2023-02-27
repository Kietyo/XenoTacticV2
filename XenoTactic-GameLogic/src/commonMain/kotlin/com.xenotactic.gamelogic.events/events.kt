package com.xenotactic.gamelogic.events

import com.xenotactic.ecs.EntityId

data class AddedMonsterEntityEvent(
    val entityId: EntityId
)

data class RemovedTowerEntityEvent(
    val entityId: EntityId
)

data class AddedEntityEvent(
    val entityId: EntityId
)