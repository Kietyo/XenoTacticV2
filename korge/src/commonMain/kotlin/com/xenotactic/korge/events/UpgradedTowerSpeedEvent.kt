package com.xenotactic.korge.events

import com.xenotactic.ecs.EntityId

data class UpgradedTowerSpeedEvent(
    val towerId: EntityId,
    val newWeaponSpeedMillis: Double,
    val newAttacksPerSecond: Double,
    val newSpeedUpgrade: Int
) {
}