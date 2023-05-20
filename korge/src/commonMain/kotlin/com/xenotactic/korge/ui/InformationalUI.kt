package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.korge.components.GameMapControllerEComponent
import korlibs.event.EventListener
import korlibs.event.ReshapeEvent
import korlibs.korge.view.*
import korlibs.math.geom.Point
import korlibs.math.roundDecimalPlaces

class InformationalUI(
    val view: Container, val engine: Engine,
    val eventBus: EventBus
) {
    val gameMapControllerComponent = engine.injections.getSingleton<GameMapControllerEComponent>()
    val pathText: Text

    init {
        pathText = view.text("")
        val globalArea = view.getVisibleWindowArea()
        reSizeernal(globalArea.widthD, globalArea.heightD)

        handlePathChanged()

        eventBus.register<UpdatedPathLineEvent> {
            updateTextWithPathLength(it.newPathLength)
        }
    }

    fun handlePathChanged() {
        updateTextWithPathLength(gameMapControllerComponent.shortestPath?.pathLength)
    }

    fun updateTextWithPathLength(pathLength: GameUnit?) {
        if (pathLength == null) {
            pathText.text = "Path Length: N/A"
        } else {
            pathText.text = "Path Length: ${pathLength.toDouble().roundDecimalPlaces(2)}"
        }
    }

    fun setup(eventListener: EventListener) {
        eventListener.onEvent(ReshapeEvent) {
            resized(it.width, it.height)
        }
    }

    private fun resized(width: Int, height: Int) {
        reSizeernal(width.toDouble(), height.toDouble())
    }

    private fun reSizeernal(width: Double, height: Double) {
        val visibleLocalArea = view.getVisibleLocalArea()
        val localArea = view.globalToLocal(Point(width, height))
        pathText.xy(visibleLocalArea.xD, localArea.yD - pathText.height)
    }
}