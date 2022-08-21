package com.xenotactic.korge.state

import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.ResizeMapEvent

class GameMapDimensionsState(
    val engine: Engine,
    width: Int,
    height: Int
) {
    var width: Int = width
        private set

    var height: Int = height
        private set

    fun changeDimensions(newWidth: Int, newHeight: Int) {
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