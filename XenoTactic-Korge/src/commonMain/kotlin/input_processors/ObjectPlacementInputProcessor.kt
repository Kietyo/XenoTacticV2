package input_processors

import com.soywiz.kmem.clamp
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Camera
import com.soywiz.korge.view.Views
import com.soywiz.korma.geom.Point
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import components.GameMapComponent
import components.ObjectPlacementComponent
import engine.Engine
import ui.UIMap
import kotlin.math.floor
import kotlin.math.roundToInt

class ObjectPlacementInputProcessor(
    override val view: BaseView,
    val engine: Engine,
    val camera: Camera,
    val gridSize: Double
) : MouseComponent {
    val objectPlacementComponent = engine.getOneTimeComponent<ObjectPlacementComponent>()
    val mapRendererComponent = engine.getOneTimeComponent<UIMap>()
    val gameMapComponent = engine.getOneTimeComponent<GameMapComponent>()

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        val localXY = camera.globalToLocalXY(event.x.toDouble(), event.y.toDouble())
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

        mapRendererComponent.renderHighlightingForPointerAction(objectPlacementComponent.pointerAction)
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
                is PointerAction.RemoveRockAtPlace -> {
                    val data = pointerAction.data
                    if (data != null) {
                        gameMapComponent.removeEntity(
                            data.entity
                        )
                        pointerAction.data = null
                        mapRendererComponent.renderHighlightingForPointerAction(pointerAction)
                    }
                }
                is PointerAction.RemoveTowerAtPlace -> {
                    val data = pointerAction.data
                    if (data != null) {
                        gameMapComponent.removeEntity(data.entity)
                        pointerAction.data = null
                        mapRendererComponent.renderHighlightingForPointerAction(pointerAction)
                    }
                }
                else -> TODO("Unsupported: $pointerAction")
            }
        }
    }

    fun mouseMoved(screenX: Double, screenY: Double) {
        val unprojected = Point(
            screenX,
            gameMapComponent.height * gridSize - screenY
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
                mapRendererComponent.renderHighlightingForPointerAction(pointerAction)
            }
            is PointerAction.RemoveRockAtPlace -> {
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

                val firstRockAtPoint =
                    gameMapComponent.getFirstRockAt(roundedGridX, roundedGridY)

                if (firstRockAtPoint == null) {
                    pointerAction.data = null
                } else {
                    pointerAction.data = RemoveRockData(
                        roundedGridX,
                        roundedGridY,
                        firstRockAtPoint
                    )
                }
            }
            is PointerAction.RemoveTowerAtPlace -> {
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

                val firstRockAtPoint =
                    gameMapComponent.getFirstTowerAt(roundedGridX, roundedGridY)

                if (firstRockAtPoint == null) {
                    pointerAction.data = null
                } else {
                    pointerAction.data = RemoveTowerData(
                        roundedGridX,
                        roundedGridY,
                        firstRockAtPoint
                    )
                }
            }
            else -> TODO("Unsupported: $pointerAction")
        }
    }
}
