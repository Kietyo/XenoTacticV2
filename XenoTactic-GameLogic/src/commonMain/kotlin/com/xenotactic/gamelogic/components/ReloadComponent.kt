package com.xenotactic.gamelogic.components

data class ReloadComponent(
    val reloadTimeMillis: Double,
    var currentDowntimeMillis: Double
) {
}