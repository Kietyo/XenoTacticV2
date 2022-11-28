package com.xenotactic.korge.input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Views
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.korge_utils.StagingEntityUtils
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.ui.NotificationTextUpdateEvent
import com.xenotactic.korge.ui.UIMapV2
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class EditorPlacementInputProcessorV2(
    override val view: BaseView,
    val engine: Engine
) : MouseComponent {
    private val editorState = engine.injections.getSingleton<EditorState>()
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val uiMap = engine.injections.getSingleton<UIMapV2>()

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

    var stagingTeleportIn: StagingEntity? = null

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
                MapEntityType.TELEPORT_OUT,
                MapEntityType.TOWER
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
                    stagingTeleportIn = entityToAdd
                    editorState.entityTypeToPlace = MapEntityType.TELEPORT_OUT
                    uiMap.renderHighlightEntity(entityToAdd)
                    engine.eventBus.send(
                        NotificationTextUpdateEvent(
                            gameMapApi.getNotificationText(MapEntityType.TELEPORT_OUT)
                        )
                    )
                    return
                }
                if (editorState.entityTypeToPlace == MapEntityType.TELEPORT_OUT) {
                    require(stagingTeleportIn != null)

                    gameMapApi.placeEntities(
                        stagingTeleportIn!!,
                        entityToAdd
                    )
                    engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
                    uiMap.clearHighlightLayer()
                    return
                }
                gameMapApi.placeEntities(entityToAdd)
                engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
            }
        } else {
            TODO("Unsupported entity type: ${editorState.entityTypeToPlace}")
        }
    }

    fun createEntityToAdd(entityType: MapEntityType, gridXInt: GameUnit, gridYInt: GameUnit): StagingEntity {
        val position = gridXInt tup gridYInt
        return when (entityType) {
            MapEntityType.START -> StagingEntityUtils.createStart(position)
            MapEntityType.FINISH -> StagingEntityUtils.createFinish(position)
            MapEntityType.CHECKPOINT -> StagingEntityUtils.createCheckpoint(
                gameMapApi.numCheckpoints,
                position
            )
            MapEntityType.ROCK -> TODO()
            MapEntityType.TOWER -> StagingEntityUtils.createTower(position)
            MapEntityType.TELEPORT_IN -> StagingEntityUtils.createTeleportIn(gameMapApi.numCompletedTeleports, position)
            MapEntityType.TELEPORT_OUT -> StagingEntityUtils.createTeleportOut(gameMapApi.numCompletedTeleports, position)
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
            MapEntityType.MONSTER -> TODO()
        }
    }

    private fun handleRockPlacement(eventType: MouseEvent.Type, gridX: Double, gridY: Double) {
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

        val width = max(highGridX.toInt() - lowGridX.toInt(), 1).toGameUnit()
        val height = max(highGridY.toInt() - lowGridY.toInt(), 1).toGameUnit()

        val roundedGridX = lowGridX.toInt().toGameUnit()
        val roundedGridY = lowGridY.toInt().toGameUnit()

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
            gameMapApi.placeEntities(
                MapEntity.Rock(roundedGridX, roundedGridY, width, height)
            )

            // Resets the highlight rectangle to the current cursor position
            handleRockPlacement(MouseEvent.Type.MOVE, gridX, gridY)
        } else {
            uiMap.renderEntityHighlightRectangle(roundedGridX, roundedGridY, width, height)
        }
    }

}