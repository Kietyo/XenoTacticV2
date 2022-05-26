package input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import components.EditorEComponent
import components.GameMapControllerEComponent
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

data class PlacedEntityEvent(val entityType: MapEntityType)

class EditorPlacementMouseKomponent(
    override val view: BaseView,
    val uiMap: UIMap,
    val engine: Engine
) : MouseComponent {
    val editorComponent = engine.getOneTimeComponent<EditorEComponent>()
    val gameMapControllerComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()

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

        val globalXY = views.globalMouseXY
        val (gridX, gridY) = uiMap.getGridPositionsFromGlobalMouse(globalXY.x, globalXY.y)

        if (editorComponent.entityTypeToPlace == MapEntityType.ROCK) {
            handleRockPlacement(event.type, gridX, gridY)
        } else if (editorComponent.entityTypeToPlace in setOf(
                MapEntityType.START,
                MapEntityType.FINISH,
                MapEntityType.CHECKPOINT,
            )
        ) {
            val (entityWidth, entityHeight) = MapEntityType.getEntitySize(editorComponent.entityTypeToPlace) as MapEntityType.EntitySize.Fixed
            val (gridXInt, gridYInt) = uiMap.getRoundedGridCoordinates(
                gridX,
                gridY,
                entityWidth,
                entityHeight
            )
            uiMap.renderHighlightRectangle(gridXInt, gridYInt, entityWidth, entityHeight)
            if (event.type == MouseEvent.Type.UP) {
                val entityToAdd =
                    createEntityToAdd(
                        editorComponent.entityTypeToPlace,
                        gridXInt,
                        gridYInt
                    )
                gameMapControllerComponent.placeEntity(entityToAdd)
                engine.eventBus.send(PlacedEntityEvent(editorComponent.entityTypeToPlace))
            }
        } else {
            TODO("Unsupported entity type: ${editorComponent.entityTypeToPlace}")
        }
    }

    fun createEntityToAdd(entityType: MapEntityType, gridXInt: Int, gridYInt: Int): MapEntity {
        return when(entityType) {
            MapEntityType.START,
            MapEntityType.FINISH -> MapEntityType.createEntity(
                editorComponent.entityTypeToPlace,
                gridXInt,
                gridYInt
            )
            MapEntityType.CHECKPOINT -> {
                MapEntity.CheckPoint(gameMapControllerComponent.numCheckpoints, gridXInt, gridYInt)
            }
            MapEntityType.ROCK -> TODO()
            MapEntityType.TOWER -> TODO()
            MapEntityType.TELEPORT_IN -> TODO()
            MapEntityType.TELEPORT_OUT -> TODO()
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
        }
    }

    fun handleRockPlacement(eventType: MouseEvent.Type, gridX: Double, gridY: Double) {
        if (eventType == MouseEvent.Type.DOWN ||
            eventType == MouseEvent.Type.MOVE
        ) {
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

        //        println(
        //            """
        //            eventType: $eventType,
        //            downGridX: $lowGridX, downGridY: $lowGridY,
        //            lastGridX: $highGridX, lastGridY: $highGridY,
        //            width: $width, height: $height,
        //            roundedGridX: $roundedGridX, roundedGridY: $roundedGridY
        //        """.trimIndent()
        //        )

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
            handleRockPlacement(MouseEvent.Type.MOVE, gridX, gridY)
        } else {
            uiMap.renderHighlightRectangle(roundedGridX, roundedGridY, width, height)
        }
    }

}