package com.xenotactic.korge.ui

import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.getVisibleLocalArea
import com.soywiz.korge.view.getVisibleWindowArea
import com.soywiz.korge.view.text
import com.soywiz.korge.view.xy
import com.soywiz.korma.math.roundDecimalPlaces
import com.xenotactic.korge.components.GameMapControllerEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.UpdatedPathLengthEvent

class InformationalUI(
    override val view: Container, val engine: Engine,
    val eventBus: EventBus
) : ResizeComponent {
    val gameMapControllerComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()
    val pathText: Text

    init {
        pathText = view.text("")
        val globalArea = view.getVisibleWindowArea()
        resizeInternal(globalArea.width, globalArea.height)

        handlePathChanged()

        eventBus.register<UpdatedPathLengthEvent> {
            updateTextWithPathLength(it.newPathLength)
        }
    }

    fun handlePathChanged() {
        updateTextWithPathLength(gameMapControllerComponent.shortestPath?.pathLength)
    }

    fun updateTextWithPathLength(pathLength: Double?) {
        if (pathLength == null) {
            pathText.text = "Path Length: N/A"
        } else {
            pathText.text = "Path Length: ${pathLength.roundDecimalPlaces(2)}"
        }
    }

    override fun resized(views: Views, width: Int, height: Int) {
        resizeInternal(width.toDouble(), height.toDouble())
    }

    fun resizeInternal(width: Double, height: Double) {
        val visibleLocalArea = view.getVisibleLocalArea()
        val localArea = view.globalToLocalXY(width, height)
        pathText.xy(visibleLocalArea.x, localArea.y - pathText.height)
    }
}