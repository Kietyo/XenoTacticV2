package com.xenotactic.korge.state

import com.xenotactic.gamelogic.utils.State
import korlibs.korge.view.View

data class DeadUIZonesState(
    val zones: MutableList<View> = mutableListOf()
) : State