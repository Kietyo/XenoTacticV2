package input_processors

import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import com.soywiz.korio.async.Signal
import com.soywiz.korma.geom.Point
import components.EditorComponent
import engine.Engine
import ui.UIMap

data class MouseEventWithGridCoordinates(
    // Origin starts at left
    val gridX: Double,
    // Origin starts at bottom
    val gridY: Double,
    val event: MouseEvent
)

class EditorPlacementMouseComponent(
    override val view: BaseView,
    val uiMapView: UIMap,
    val engine: Engine
): MouseComponent {
    val editorComponent = engine.getOneTimeComponent<EditorComponent>()
    val gridSize: Double
        get() = this.uiMapView._gridSize

    lateinit var lastEvent: MouseEvent
    val onMouseEventWithGridCoordinates = Signal<MouseEventWithGridCoordinates>()

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        println("event: $event")
        val localXY = uiMapView.globalToLocalXY(event.x.toDouble(), event.y.toDouble())
        val unprojected = Point(
            localXY.x,
            uiMapView.mapHeight * gridSize - localXY.y
        )

        val gridX = unprojected.x / gridSize
        val gridY = unprojected.y / gridSize

        println("gridX: $gridX, gridY: $gridY")

        onMouseEventWithGridCoordinates(
            MouseEventWithGridCoordinates(
                gridX, gridY, event
            )
        )
    }

}