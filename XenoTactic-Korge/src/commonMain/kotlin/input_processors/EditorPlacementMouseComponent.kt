package input_processors

import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import components.EditorComponent
import engine.Engine
import ui.UIMap
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

data class MouseEventWithGridCoordinates(
    // Origin starts at left
    val gridX: Double,
    // Origin starts at bottom
    val gridY: Double,
    val event: MouseEvent
)

class EditorPlacementMouseComponent(
    override val view: BaseView,
    val uiMap: UIMap,
    val engine: Engine
): MouseComponent {
    val editorComponent = engine.getOneTimeComponent<EditorComponent>()
    val gridSize: Double
        get() = this.uiMap._gridSize

    lateinit var downEvent: MouseEvent
    lateinit var currentEvent: MouseEvent

    val ALLOWED_EVENTS = setOf<MouseEvent.Type>(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
    )

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (!editorComponent.isEditingEnabled ||
                !ALLOWED_EVENTS.contains(event.type)) {
            return
        }

        if (event.type == MouseEvent.Type.UP) {
            uiMap.hideHighlightRectangle()
            return
        }

        if (event.type == MouseEvent.Type.DOWN) {
            downEvent = event.copy()
        }

        currentEvent = event.copy()
        val (downGridX, downGridY) = uiMap.getGridPositionsFromGlobalMouse(downEvent)
        val (lastGridX, lastGridY) = uiMap.getGridPositionsFromGlobalMouse(currentEvent)

        val lowGridX = min(downGridX, lastGridX)
        val lowGridY = min(downGridY, lastGridY)

        val highGridX = ceil(max(downGridX, lastGridX))
        val highGridY = ceil(max(downGridY, lastGridY))

        val width = max(highGridX.toInt() - lowGridX.toInt(), 1)
        val height = max(highGridY.toInt() - lowGridY.toInt(), 1)

        val roundedGridX = lowGridX.toInt()
        val roundedGridY = lowGridY.toInt()

        println("""
            event: $event,
            downGridX: $lowGridX, downGridY: $lowGridY,
            lastGridX: $highGridX, lastGridY: $highGridY,
            width: $width, height: $height,
            roundedGridX: $roundedGridX, roundedGridY: $roundedGridY
        """.trimIndent())

//        println("gridX: $gridX, gridY: $gridY")

//        onMouseEventWithGridCoordinates(
//            MouseEventWithGridCoordinates(
//                gridX, gridY, event
//            )
//        )

        uiMap.renderHighlightRectangle(roundedGridX, roundedGridY, width, height)
    }

}