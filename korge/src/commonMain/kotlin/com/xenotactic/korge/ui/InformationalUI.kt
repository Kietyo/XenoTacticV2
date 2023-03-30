package com.xenotactic.korge.ui

import com.soywiz.korev.EventListener
import com.soywiz.korev.ReshapeEvent
import korlibs.korge.view.Container
import korlibs.korge.view.Text
import korlibs.korge.view.getVisibleLocalArea
import korlibs.korge.view.getVisibleWindowArea
import korlibs.korge.view.text
import korlibs.korge.view.xy
import korlibs.math.geom.Point
import korlibs.math.math.roundDecimalPlaces
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.korge.ecomponents.GameMapControllerEComponent
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent

class InformationalUI(
    val view: Container, val engine: Engine,
    val eventBus: EventBus
) {
    val gameMapControllerComponent = engine.injections.getSingleton<GameMapControllerEComponent>()
    val pathText: Text

    init {
        pathText = view.text("")
        val globalArea = view.getVisibleWindowArea()
        resizeInternal(globalArea.width, globalArea.height)

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
        resizeInternal(width.toDouble(), height.toDouble())
    }

    private fun resizeInternal(width: Double, height: Double) {
        val visibleLocalArea = view.getVisibleLocalArea()
        val localArea = view.globalToLocal(Point(width, height))
        pathText.xy(visibleLocalArea.x, localArea.y - pathText.height)
    }
}