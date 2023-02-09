package com.xenotactic.korge.state

import com.soywiz.korge.view.View
import com.xenotactic.korge.engine.State

data class DeadUIZonesState(
    val zones: MutableList<View> = mutableListOf()
) : State