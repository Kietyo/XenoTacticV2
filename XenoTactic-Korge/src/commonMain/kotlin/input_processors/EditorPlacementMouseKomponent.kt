package input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import com.xenotactic.gamelogic.model.MapEntity
import components.EditorComponent
import components.GameMapControllerComponent
import engine.Engine
import ui.UIMap
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class MouseEventWithGridCoordinates(
    // Origin starts at left
    val gridX: Double,
    // Origin starts at bottom
    val gridY: Double,
    val event: MouseEvent
)

class EditorPlacementMouseKomponent(
    override val view: BaseView,
    val uiMap: UIMap,
    val engine: Engine
) : MouseComponent {
    val editorComponent = engine.getOneTimeComponent<EditorComponent>()
    val gameMapControllerComponent = engine.getOneTimeComponent<GameMapControllerComponent>()

    val ALLOWED_EVENTS = setOf(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
        MouseEvent.Type.MOVE
    )

    var downGridX: Double = 0.0
    var downGridY: Double = 0.0

    var currentGridX = 0.0
    var currentGridY = 0.0

    var isRightClickDrag = false

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (!editorComponent.isEditingEnabled ||
            !ALLOWED_EVENTS.contains(event.type)
        ) {
            return
        }

        if (event.type == MouseEvent.Type.DOWN && event.button == MouseButton.RIGHT) {
            isRightClickDrag = true
            return
        }

        if (event.type == MouseEvent.Type.UP && event.button == MouseButton.RIGHT) {
            isRightClickDrag = false
            return
        }

        if (isRightClickDrag) {
            return
        }

        println(event)

        val globalXY = views.globalMouseXY
        val gridPositions = uiMap.getGridPositionsFromGlobalMouse(globalXY.x, globalXY.y)

        handle(event.type, gridPositions.first, gridPositions.second)
    }

    fun handle(eventType: MouseEvent.Type, gridX: Double, gridY: Double) {
        if (eventType == MouseEvent.Type.DOWN ||
            eventType == MouseEvent.Type.MOVE) {
            downGridX = gridX
            downGridY = gridY
        }

        currentGridX = gridX
        currentGridY = gridY

        val lowGridX = min(downGridX, currentGridX)
        val lowGridY = min(downGridY, currentGridY)

        val highGridX = ceil(max(downGridX, currentGridX))
        val highGridY = ceil(max(downGridY, currentGridY))

        val width = max(highGridX.toInt() - lowGridX.toInt(), 1)
        val height = max(highGridY.toInt() - lowGridY.toInt(), 1)

        val roundedGridX = lowGridX.toInt()
        val roundedGridY = lowGridY.toInt()

        println(
            """
            eventType: $eventType,
            downGridX: $lowGridX, downGridY: $lowGridY,
            lastGridX: $highGridX, lastGridY: $highGridY,
            width: $width, height: $height,
            roundedGridX: $roundedGridX, roundedGridY: $roundedGridY
        """.trimIndent()
        )

        //        println("gridX: $gridX, gridY: $gridY")

        //        onMouseEventWithGridCoordinates(
        //            MouseEventWithGridCoordinates(
        //                gridX, gridY, event
        //            )
        //        )

        if (eventType == MouseEvent.Type.UP) {
            gameMapControllerComponent.placeEntity(
                MapEntity.Rock(
                    roundedGridX,
                    roundedGridY,
                    width,
                    height
                )
            )

            // Resets the highlight rectangle to the current cursor position
            handle(MouseEvent.Type.MOVE, gridX, gridY)
        } else {
            uiMap.renderHighlightRectangle(roundedGridX, roundedGridY, width, height)
        }
    }

}