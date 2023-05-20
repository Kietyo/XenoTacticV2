package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.utils.Engine
import korlibs.korge.view.Container
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.text

class UIDebugInfo(
    val engine: Engine
) : Container() {
    val eventBus = engine.eventBus
    val infoText = text("Hello world")
    val pathLengthText = text("Hello world") {
        alignTopToBottomOf(infoText)
        eventBus.register<UpdatedPathLineEvent> {
            text = "Path length: ${it.newPathLength?.toInt()}"
        }
    }
}