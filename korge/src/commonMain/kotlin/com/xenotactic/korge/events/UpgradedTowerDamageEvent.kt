package com.xenotactic.korge.events

import com.xenotactic.ecs.EntityId

data class UpgradedTowerDamageEvent(
    val towerId: EntityId,
    val newDamage: Double,
    val newDamageUpgrade: Int
)
