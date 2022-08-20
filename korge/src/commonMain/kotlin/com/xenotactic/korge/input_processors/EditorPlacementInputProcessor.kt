package com.xenotactic.korge.input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.ecomponents.GameMapControllerEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.ui.NotificationTextUpdateEvent
import com.xenotactic.korge.ui.UIMap
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

class EditorPlacementInputProcessor(
    override val view: BaseView,
    val uiMap: UIMap,
    val engine: Engine
) : MouseComponent {
    private val editorState = engine.injections.getSingleton<EditorState>()
    private val gameMapControllerComponent =
        engine.injections.getSingleton<GameMapControllerEComponent>()

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

    var stagingTeleportIn: MapEntity.TeleportIn? = null

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (!editorState.isEditingEnabled ||
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

        if (editorState.entityTypeToPlace == MapEntityType.ROCK) {
            handleRockPlacement(event.type, gridX, gridY)
        } else if (editorState.entityTypeToPlace in setOf(
                MapEntityType.START,
                MapEntityType.FINISH,
                MapEntityType.CHECKPOINT,
                MapEntityType.TELEPORT_IN,
                MapEntityType.TELEPORT_OUT
            )
        ) {
            val (entityWidth, entityHeight) = MapEntityType.getEntitySize(editorState.entityTypeToPlace) as MapEntityType.EntitySize.Fixed
            val (gridXInt, gridYInt) = uiMap.getRoundedGridCoordinates(
                gridX,
                gridY,
                entityWidth,
                entityHeight
            )
            uiMap.renderEntityHighlightRectangle(gridXInt, gridYInt, entityWidth, entityHeight)
            if (event.type == MouseEvent.Type.UP) {
                val entityToAdd =
                    createEntityToAdd(
                        editorState.entityTypeToPlace,
                        gridXInt,
                        gridYInt
                    )
                if (editorState.entityTypeToPlace == MapEntityType.TELEPORT_IN) {
                    stagingTeleportIn = entityToAdd as MapEntity.TeleportIn
                    editorState.entityTypeToPlace = MapEntityType.TELEPORT_OUT
                    uiMap.renderHighlightEntity(entityToAdd)
                    engine.eventBus.send(
                        NotificationTextUpdateEvent(
                            gameMapControllerComponent.getNotificationText(MapEntityType.TELEPORT_OUT)
                        )
                    )
                    return
                }
                if (editorState.entityTypeToPlace == MapEntityType.TELEPORT_OUT) {
                    require(stagingTeleportIn != null)
                    gameMapControllerComponent.placeEntities(
                        stagingTeleportIn!!,
                        entityToAdd
                    )
                    engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
                    uiMap.clearHighlightLayer()
                    return
                }
                gameMapControllerComponent.placeEntity(entityToAdd)
                engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
            }
        } else {
            TODO("Unsupported entity type: ${editorState.entityTypeToPlace}")
        }
    }

    fun createEntityToAdd(entityType: MapEntityType, gridXInt: Int, gridYInt: Int): MapEntity {
        return when (entityType) {
            MapEntityType.START,
            MapEntityType.FINISH -> MapEntityType.createEntity(
                editorState.entityTypeToPlace,
                gridXInt,
                gridYInt
            )

            MapEntityType.CHECKPOINT -> {
                MapEntity.Checkpoint(gameMapControllerComponent.numCheckpoints, gridXInt, gridYInt)
            }

            MapEntityType.ROCK -> TODO()
            MapEntityType.TOWER -> TODO()
            MapEntityType.TELEPORT_IN -> {
                MapEntity.TeleportIn(gameMapControllerComponent.numTeleports, gridXInt, gridYInt)
            }

            MapEntityType.TELEPORT_OUT -> {
                MapEntity.TeleportOut(gameMapControllerComponent.numTeleports, gridXInt, gridYInt)
            }

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
            uiMap.renderEntityHighlightRectangle(roundedGridX, roundedGridY, width, height)
        }
    }

}