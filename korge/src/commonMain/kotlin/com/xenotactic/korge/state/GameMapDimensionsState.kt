package com.xenotactic.korge.state

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.engine.State
import com.xenotactic.korge.events.ResizeMapEvent

class GameMapDimensionsState(
    val engine: Engine,
    width: GameUnit,
    height: GameUnit
) : State {
    var width: GameUnit = width
        private set

    var height: GameUnit = height
        private set

    fun changeDimensions(newWidth: GameUnit, newHeight: GameUnit) {
        if (width == newWidth && height == newHeight) return
        val oldMapWidth = width
        val oldMapHeight = height
        width = newWidth
        height = newHeight
        engine.eventBus.send(ResizeMapEvent(
            oldMapWidth,
            oldMapHeight,
            newWidth,
            newHeight
        ))
    }
}