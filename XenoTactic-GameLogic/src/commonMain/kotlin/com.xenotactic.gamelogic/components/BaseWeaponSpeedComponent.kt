package com.xenotactic.gamelogic.components

// https://sc2-coop.fandom.com/wiki/Weapon_Speed
data class BaseWeaponSpeedComponent(
    // Suppose that this is 860.
    // This means that the unit must wait 0.86 seconds
    // before it can attack again.
    val millis: Double
) {
}