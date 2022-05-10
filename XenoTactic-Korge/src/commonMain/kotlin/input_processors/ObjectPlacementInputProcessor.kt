package input_processors

import com.soywiz.kmem.clamp
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import components.ObjectPlacementComponent
import components.UIMapControllerComponent
import engine.Engine
import ui.UIMap
import kotlin.math.floor
import kotlin.math.roundToInt

data class MouseEventWithGridCoordinates(
    // Origin starts at left
    val gridX: Double,
    // Origin starts at bottom
    val gridY: Double,
    val event: MouseEvent
)

class ObjectPlacementInputProcessor(
    override val view: View,
    val uiMapView: UIMap,
    val engine: Engine,
) : MouseComponent {
    val objectPlacementComponent = engine.getOneTimeComponent<ObjectPlacementComponent>()
    val gameMapComponent = engine.getOneTimeComponent<UIMapControllerComponent>()

    val gridSize: Double
        get() = uiMapView._gridSize

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        val localXY = uiMapView.globalToLocalXY(event.x.toDouble(), event.y.toDouble())
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
                //                    camera.globalToLocalXY(event.x.toDouble(), event.y.toDouble()): ${
                //                        camera.globalToLocalXY(
                //                            event.x.toDouble(),
                //                            event.y.toDouble()
                //                        )
                //                    }
                //                """.trimIndent()
                //                )
            }
            MouseEvent.Type.MOVE -> {
                mouseMoved(localXY.x, localXY.y)
            }
        }

        uiMapView.renderHighlightingForPointerAction(objectPlacementComponent.pointerAction)
    }

    fun touchDown(button: MouseButton) {
        //        println("screenX: $screenX, screenY: $screenY, pointer: $pointer, button: $button")
        val pointerAction = objectPlacementComponent.pointerAction
        if (button == MouseButton.LEFT) {
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
                        is MapEntity.CheckPoint -> TODO()
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

    fun mouseMoved(screenX: Double, screenY: Double) {
        println("screenX: $screenX, screenY: $screenY")
//        val relativePoint = view.getPointRelativeTo(Point(screenX, screenY), uiMapView)
//        val relativePoint = uiMapView.getPositionRelativeTo(view)
//        val relativePoint = uiMapView._boardLayer.globalToLocalXY(screenX, screenY)
//        val relativePoint = uiMapView.localToGlobalXY(screenX, screenY)

//        println("relativePoint: $relativePoint")

        val relativePoint = Point(screenX, screenY)
        val unprojected = Point(
            screenX,
            uiMapView.mapHeight * gridSize - screenY
        )

        val gridX = unprojected.x / gridSize
        val gridY = unprojected.y / gridSize

        //        println("gridX: $gridX, gridY: $gridY")

        val pointerAction = objectPlacementComponent.pointerAction

        when (pointerAction) {
            PointerAction.Inactive -> {
                return
            }
            is PointerAction.HighlightForPlacement -> {
                val currentEntity = pointerAction.mapEntity
                val roundedGridX = when {
                    currentEntity.width == 1 -> floor(
                        gridX - (currentEntity.width) / 2
                    ).toInt()
                    else -> (gridX - (currentEntity.width) / 2).roundToInt()
                }

                val roundedGridY = when {
                    currentEntity.height == 1 -> floor(
                        gridY - (currentEntity.height) / 2
                    ).toInt()
                    else -> (gridY - (currentEntity.height) / 2).roundToInt()
                }

                val gridXToInt = roundedGridX.clamp(
                    -currentEntity.width,
                    gameMapComponent.width - 1 + currentEntity.width
                )
                val gridYToInt = roundedGridY.clamp(
                    -currentEntity.height,
                    gameMapComponent.height - 1 + currentEntity.height
                )

                if ((gridXToInt !in 0 until gameMapComponent.width)
                    || gridYToInt !in 0 until gameMapComponent.height
                ) {
                    pointerAction.placementLocation = null
                    return
                }

                pointerAction.placementLocation = IntPoint(gridXToInt, gridYToInt)
                uiMapView.renderHighlightingForPointerAction(pointerAction)
            }
            is PointerAction.RemoveEntityAtPlace -> {
                val roundedGridX = floor(
                    gridX
                ).toInt()

                val roundedGridY = floor(
                    gridY
                ).toInt()

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
