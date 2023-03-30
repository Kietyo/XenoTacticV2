package com.xenotactic.korge.state

import korlibs.korge.view.View
import com.xenotactic.gamelogic.engine.State

data class DeadUIZonesState(
    val zones: MutableList<View> = mutableListOf()
) : State