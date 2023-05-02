package com.xenotactic.gamelogic.state

import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.State
import com.xenotactic.gamelogic.events.ResizeMapEvent
import com.xenotactic.gamelogic.utils.GameUnit

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
        engine.eventBus.send(
            ResizeMapEvent(
                oldMapWidth,
                oldMapHeight,
                newWidth,
                newHeight
            )
        )
    }
}