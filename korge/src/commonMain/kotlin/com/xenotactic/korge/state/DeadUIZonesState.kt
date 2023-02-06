package com.xenotactic.korge.state

import com.soywiz.korge.view.View

data class DeadUIZonesState(
    val zones: MutableList<View> = mutableListOf()
) {
}