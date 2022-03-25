package ui

import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.view.*
import com.soywiz.korma.math.roundDecimalPlaces
import components.GameMapComponent
import engine.Engine
import events.EventBus
import events.UpdatedPathLengthEvent

class InformationalUI(
    override val view: Container, val engine: Engine,
    val eventBus: EventBus
) : ResizeComponent {
    val gameMapComponent = engine.getOneTimeComponent<GameMapComponent>()
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
        updateTextWithPathLength(gameMapComponent.shortestPath?.pathLength)
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