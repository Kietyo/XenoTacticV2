package com.xenotactic.korge.components

data class ReloadComponent(
    val reloadTimeMillis: Double,
    var currentDowntimeMillis: Double
) {
}