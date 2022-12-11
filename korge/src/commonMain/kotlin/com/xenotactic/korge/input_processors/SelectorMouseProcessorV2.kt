package com.xenotactic.korge.input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Views
import com.soywiz.korge.view.alpha
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.visible
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.PreSelectionComponent
import com.xenotactic.gamelogic.components.SelectedComponent
import com.xenotactic.korge.engine.EComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.state.GameMapApi

class SelectorMouseProcessorV2(
    override val view: Container,
    val engine: Engine,
    var isEnabled: Boolean = true
) :
    MouseComponent, EComponent {

    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()

    private val selectionRectangle = view.solidRect(0, 0, Colors.BLUE).alpha(0.25).visible(false)

    private var dragging = false
    private var isInitialClick = false

    private var startPosition = Point()
    private var currentPosition = Point()

    fun reset() {
        dragging = false
        isInitialClick = false
    }

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (!isEnabled) return
        if (event.type == MouseEvent.Type.MOVE) return

        if (event.type == MouseEvent.Type.DOWN &&
            event.button == MouseButton.LEFT
        ) {
            dragging = true
            isInitialClick = true
            startPosition.copyFrom(views.globalMouseXY)
        }

        if (event.type == MouseEvent.Type.CLICK &&
            event.button == MouseButton.LEFT
        ) {
            reset()
            return
        }

        currentPosition.copyFrom(views.globalMouseXY)

        if (dragging) {
            println(
                """
                startPosition: $startPosition
                currentPosition: $currentPosition
            """.trimIndent()
            )
            println("Dragging!")

            selectionRectangle.apply {
                visible = true
                scaledWidth = currentPosition.x - startPosition.x
                scaledHeight = currentPosition.y - startPosition.y
                xy(startPosition)
            }

            val intersectingEntities =
                gameMapApi.getIntersectingEntities(selectionRectangle.getGlobalBounds())
            println("intersectingEntities: $intersectingEntities")
            engine.gameWorld.preSelectionFamily.getNewList().forEach {
                engine.gameWorld.world.modifyEntity(it) {
                    removeComponent<com.xenotactic.gamelogic.components.PreSelectionComponent>()
                }
            }

            if (event.type == MouseEvent.Type.UP &&
                event.button == MouseButton.LEFT
            ) {
                dragging = false
                selectionRectangle.visible = false

                //                val topLeft = getTopLeft(startPosition, currentPosition)
                //                val bottomRight = getBottomRight(startPosition, currentPosition)
                //
                //                println("""
                //                    topLeft: $topLeft
                //                    bottomRight: $bottomRight
                //                    selectionRectangle.getVisibleGlobalArea(): ${selectionRectangle.getVisibleGlobalArea()}
                //                    selectionRectangle.getBounds(): ${selectionRectangle.getBounds()}
                //                    selectionRectangle.getGlobalBounds(): ${selectionRectangle.getGlobalBounds()}
                //                    selectionRectangle.getLocalBounds(): ${selectionRectangle.getLocalBounds()}
                //                """.trimIndent())
                //
                //                println(uiMap.getIntersectingEntities(selectionRectangle.getGlobalBounds()))
                engine.gameWorld.selectionFamily.getNewList().forEach {
                    engine.gameWorld.world.modifyEntity(it) {
                        this.removeComponent<com.xenotactic.gamelogic.components.SelectedComponent>()
                    }
                }

                intersectingEntities.forEach {
                    engine.gameWorld.world.modifyEntity(it) {
                        addIfNotExists(com.xenotactic.gamelogic.components.SelectedComponent)
                    }
                }
            } else {
                intersectingEntities.forEach {
                    engine.gameWorld.world.modifyEntity(it) {
                        addIfNotExists(com.xenotactic.gamelogic.components.PreSelectionComponent)
                    }
                }
//                engine.eventBus.send(
//                    SelectedUIEntitiesEvent(
//                        SelectionType.PRE_SELECTION,
//                        previousSelectionSnapshot,
//                        intersectingEntities
//                    )
//                )
            }
//            previousSelectionSnapshot = intersectingEntities
        }

    }

}