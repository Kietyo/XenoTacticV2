package com.xenotactic.korge.input_processors

import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GameMapApi
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.ui.NotificationTextUpdateEvent
import com.xenotactic.korge.ui.UIMapV2
import com.xenotactic.korge.utils.StagingEntityUtils
import korlibs.event.EventListener
import korlibs.event.MouseButton
import korlibs.event.MouseEvent
import korlibs.korge.view.BaseView
import korlibs.korge.view.Views
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class PlacedEntityEvent(val entityType: MapEntityType)
data class PlaceEntityErrorEvent(val errorMsg: String)

class EditorPlacementInputProcessor(
    val views: Views,
    val view: BaseView,
    val engine: Engine
) {
    private val editorState = engine.stateInjections.getSingleton<EditorState>()
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val uiMap = engine.injections.getSingleton<UIMapV2>()

    private val ALLOWED_EVENTS = setOf(
        MouseEvent.Type.DOWN,
        MouseEvent.Type.DRAG,
        MouseEvent.Type.UP,
        MouseEvent.Type.MOVE
    )

    var downGridX = 0f
    var downGridY = 0f

    var currentGridX = 0f
    var currentGridY = 0f

    var isRightClickDrag = false

    var stagingTeleportIn: StagingEntity? = null

    fun setup(eventListener: EventListener) {
        eventListener.onEvents(
            MouseEvent.Type.DOWN,
            MouseEvent.Type.DRAG,
            MouseEvent.Type.UP,
            MouseEvent.Type.MOVE
        ) {
            onMouseEvent(views, it)
        }
    }

    private fun onMouseEvent(views: Views, event: MouseEvent) {
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

        val globalXY = views.globalMousePos
        val (gridX, gridY) = uiMap.getGridPositionsFromGlobalMouse(globalXY.x, globalXY.y)

        if (editorState.entityTypeToPlace == MapEntityType.ROCK) {
            handleRockPlacement(event.type, gridX, gridY)
        } else if (editorState.entityTypeToPlace in setOf(
                MapEntityType.START,
                MapEntityType.FINISH,
                MapEntityType.CHECKPOINT,
                MapEntityType.TELEPORT_IN,
                MapEntityType.TELEPORT_OUT,
                MapEntityType.TOWER,
                MapEntityType.SUPPLY_DEPOT
            )
        ) {
            val (entityWidth, entityHeight) = MapEntityType.getEntitySize(
                editorState.entityTypeToPlace
            ) as MapEntityType.EntitySize.Fixed
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
                when (editorState.entityTypeToPlace) {
                    MapEntityType.START -> TODO()
                    MapEntityType.FINISH -> TODO()
                    MapEntityType.CHECKPOINT -> TODO()
                    MapEntityType.ROCK -> TODO()
                    MapEntityType.TELEPORT_IN -> {
                        stagingTeleportIn = entityToAdd
                        editorState.entityTypeToPlace = MapEntityType.TELEPORT_OUT
                        uiMap.renderHighlightEntity(entityToAdd)
                        engine.eventBus.send(
                            NotificationTextUpdateEvent(
                                gameMapApi.getNotificationText(editorState.entityTypeToPlace)
                            )
                        )
                        return
                    }

                    MapEntityType.TELEPORT_OUT -> {
                        require(stagingTeleportIn != null)

                        gameMapApi.placeEntities(
                            stagingTeleportIn!!,
                            entityToAdd
                        )
                        engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
                        uiMap.clearHighlightLayer()
                        return
                    }

                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                    MapEntityType.MONSTER -> TODO()
                    MapEntityType.TOWER -> {
                        if (gameMapApi.checkNewEntitiesBlocksPath(entityToAdd)) {
                            engine.eventBus.send(
                                PlaceEntityErrorEvent(
                                    "Unable to place '${editorState.entityTypeToPlace}': Blocks path"
                                )
                            )
                            return
                        }
                        if (gameMapApi.checkNewEntityIntersectsExistingBlockingEntities(entityToAdd)) {
                            engine.eventBus.send(
                                PlaceEntityErrorEvent(
                                    "Unable to place '${editorState.entityTypeToPlace}': Intersects with another blocking entity"
                                )
                            )
                            return
                        }
                        if (gameMapApi.isAtMaxSupply()) {
                            engine.eventBus.send(
                                PlaceEntityErrorEvent(
                                    "Unable to place '${editorState.entityTypeToPlace}': Supply limit"
                                )
                            )
                            return
                        }
                        gameMapApi.placeEntities(entityToAdd)
                        engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
                    }

                    MapEntityType.SUPPLY_DEPOT -> {
                        if (gameMapApi.checkNewEntityIntersectsExistingBlockingEntities(entityToAdd)) {
                            engine.eventBus.send(
                                PlaceEntityErrorEvent(
                                    "Unable to place '${editorState.entityTypeToPlace}': Intersects with another blocking entity"
                                )
                            )
                            return
                        }
                        gameMapApi.placeEntities(entityToAdd)
                        engine.eventBus.send(PlacedEntityEvent(editorState.entityTypeToPlace))
                    }
                }
            }
        } else {
            TODO("Unsupported entity type: ${editorState.entityTypeToPlace}")
        }
    }

    private fun createEntityToAdd(entityType: MapEntityType, gridXInt: GameUnit, gridYInt: GameUnit): StagingEntity {
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
            MapEntityType.TELEPORT_OUT -> StagingEntityUtils.createTeleportOut(
                gameMapApi.numCompletedTeleports,
                position
            )

            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
            MapEntityType.MONSTER -> TODO()
            MapEntityType.SUPPLY_DEPOT -> StagingEntityUtils.createSupplyDepot(position)
        }
    }

    private fun handleRockPlacement(eventType: MouseEvent.Type, gridX: Float, gridY: Float) {
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
                StagingEntityUtils.createRock(
                    RectangleEntity(roundedGridX, roundedGridY, width, height)
                )
            )

            // Resets the highlight rectangle to the current cursor position
            handleRockPlacement(MouseEvent.Type.MOVE, gridX, gridY)
        } else {
            uiMap.renderEntityHighlightRectangle(roundedGridX, roundedGridY, width, height)
        }
    }

}