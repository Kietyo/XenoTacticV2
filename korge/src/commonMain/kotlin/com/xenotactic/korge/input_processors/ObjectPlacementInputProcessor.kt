package com.xenotactic.korge.input_processors

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.until
import com.xenotactic.korge.components.GameMapControllerEComponent
import com.xenotactic.korge.components.ObjectPlacementEComponent
import com.xenotactic.korge.ui.UIMap
import korlibs.event.EventListener
import korlibs.event.MouseButton
import korlibs.event.MouseEvent
import korlibs.korge.view.View
import kotlin.math.floor

class ObjectPlacementInputProcessor(
    val view: View,
    val uiMapView: UIMap,
    val engine: Engine,
    val eventBus: EventBus
) {
    val objectPlacementComponent = engine.injections.getSingleton<ObjectPlacementEComponent>()
    val gameMapComponent = engine.injections.getSingleton<GameMapControllerEComponent>()

    fun setup(eventListener: EventListener) {
        eventListener.onEvents(*MouseEvent.Type.ALL) {
            onMouseEvent(it)
        }
    }

    fun onMouseEvent(event: MouseEvent) {
        val (gridX, gridY) = uiMapView.getGridPositionsFromGlobalMouse(
            event.x.toDouble(), event.y.toDouble()
        )
        println(event)
        when (event.type) {
            MouseEvent.Type.DOWN -> {
                println(event)
                touchDown(event.button)

                //                println(
                //                    """
                //                    views: $views
                //                    event: $event,
                //                    camera.mouse.downPosGlobal: ${camera.mouse.downPosGlobal}
                //                    camera.mouse.downPosLocal: ${camera.mouse.downPosLocal}
                //                    camera.localToRenderXY(event.x.toDouble(), event.y.toDouble()): ${
                //                        camera.localToRenderXY(
                //                            event.x.toDouble(),
                //                            event.y.toDouble()
                //                        )
                //                    }
                //                    camera.globalToLocal(event.x.toDouble(), event.y.toDouble()): ${
                //                        camera.globalToLocal(
                //                            event.x.toDouble(),
                //                            event.y.toDouble()
                //                        )
                //                    }
                //                """.trimIndent()
                //                )
            }

            MouseEvent.Type.MOVE -> {
                mouseMoved(gridX, gridY)
            }

            else -> TODO()
        }

        uiMapView.renderHighlightingForPointerAction(objectPlacementComponent.pointerAction)
    }

    fun touchDown(button: MouseButton) {
        //        println("screenX: $screenX, screenY: $screenY, pointer: $pointer, button: $button")
        if (button == MouseButton.LEFT) {
            val pointerAction = objectPlacementComponent.pointerAction
            when (pointerAction) {
                PointerAction.Inactive -> {
                    return
                }

                is PointerAction.HighlightForPlacement -> {
                    if (pointerAction.placementLocation == null) {
                        return
                    }
                    when (pointerAction.mapEntity) {
                        is MapEntity.Start -> TODO()
                        is MapEntity.Finish -> TODO()
                        is MapEntity.TeleportIn -> TODO()
                        is MapEntity.TeleportOut -> TODO()
                        is MapEntity.Rock -> {
                            println("Placing: $pointerAction")
                            gameMapComponent.placeEntity(
                                pointerAction.mapEntity.at(pointerAction.placementLocation!!)
                            )
                        }

                        is MapEntity.Checkpoint -> TODO()
                        is MapEntity.Tower, is MapEntity.SmallBlocker -> {
                            val entityAtPlace =
                                pointerAction.mapEntity.at(pointerAction.placementLocation!!)
                            if (!gameMapComponent.intersectsBlockingEntities(entityAtPlace)) {
                                gameMapComponent.placeEntity(
                                    entityAtPlace
                                )
                            }
                        }

                        else -> TODO()
                    }
                }

                is PointerAction.RemoveEntityAtPlace -> {
                    val data = pointerAction.data
                    if (data != null) {
                        gameMapComponent.removeEntity(data.entity)
                        pointerAction.data = null
                        uiMapView.renderHighlightingForPointerAction(pointerAction)
                    }
                }

                else -> TODO("Unsupported: $pointerAction")
            }
        }
    }

    fun mouseMoved(gridX: Double, gridY: Double) {
        val pointerAction = objectPlacementComponent.pointerAction

        when (pointerAction) {
            PointerAction.Inactive -> {
                return
            }

            is PointerAction.HighlightForPlacement -> {
                val currentEntity = pointerAction.mapEntity
                val (gridXToInt, gridYToInt) =
                    uiMapView.getRoundedGridCoordinates(
                        gridX,
                        gridY,
                        currentEntity.width,
                        currentEntity.height
                    )

                if ((gridXToInt !in 0 until gameMapComponent.width)
                    || gridYToInt !in 0 until gameMapComponent.height
                ) {
                    pointerAction.placementLocation = null
                    return
                }

                pointerAction.placementLocation = GameUnitTuple(gridXToInt, gridYToInt)
                uiMapView.renderHighlightingForPointerAction(pointerAction)
            }

            is PointerAction.RemoveEntityAtPlace -> {
                val roundedGridX = floor(
                    gridX
                ).toInt().toGameUnit()

                val roundedGridY = floor(
                    gridY
                ).toInt().toGameUnit()

                if (pointerAction.data != null) {
                    val removeEntityData = pointerAction.data!!
                    if (removeEntityData.x == roundedGridX && removeEntityData.y == roundedGridY) {
                        return
                    }
                }

                val firstEntityAtPoint = when (pointerAction.entityType) {
                    MapEntityType.START -> TODO()
                    MapEntityType.FINISH -> TODO()
                    MapEntityType.CHECKPOINT -> TODO()
                    MapEntityType.ROCK -> gameMapComponent.getFirstRockAt(
                        roundedGridX,
                        roundedGridY
                    )

                    MapEntityType.TOWER -> gameMapComponent.getFirstTowerAt(
                        roundedGridX,
                        roundedGridY
                    )

                    MapEntityType.TELEPORT_IN -> TODO()
                    MapEntityType.TELEPORT_OUT -> TODO()
                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                    MapEntityType.MONSTER -> TODO()
                    MapEntityType.SUPPLY_DEPOT -> gameMapComponent.getFirstSupplyDepotAt(
                        roundedGridX,
                        roundedGridY
                    )
                }

                if (firstEntityAtPoint == null) {
                    pointerAction.data = null
                } else {
                    pointerAction.data = RemoveEntityData(
                        roundedGridX,
                        roundedGridY,
                        firstEntityAtPoint
                    )
                }
            }

            else -> TODO("Unsupported: $pointerAction")
        }
    }
}