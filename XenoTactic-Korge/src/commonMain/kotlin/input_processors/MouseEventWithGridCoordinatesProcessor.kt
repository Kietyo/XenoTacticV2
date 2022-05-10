package input_processors

import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import com.soywiz.korio.async.Signal
import com.soywiz.korma.geom.Point
import ui.UIMap

class MouseEventWithGridCoordinatesProcessor(
    override val view: BaseView,
    val uiMapView: UIMap,
): MouseComponent {
    val gridSize: Double
        get() = this.uiMapView._gridSize

    val onMouseEventWithGridCoordinates = Signal<MouseEventWithGridCoordinates>()

    override fun onMouseEvent(views: Views, event: MouseEvent) {
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