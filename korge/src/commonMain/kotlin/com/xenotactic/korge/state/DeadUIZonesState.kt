package com.xenotactic.korge.state

import korlibs.korge.view.View
import com.xenotactic.gamelogic.utils.State

data class DeadUIZonesState(
    val zones: MutableList<View> = mutableListOf()
) : State