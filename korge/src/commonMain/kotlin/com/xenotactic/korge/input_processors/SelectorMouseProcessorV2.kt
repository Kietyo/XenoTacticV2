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
import com.xenotactic.ecs.World
import com.xenotactic.korge.ecomponents.UIMapEComponent
import com.xenotactic.korge.engine.EComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.fleks.components.SelectionType
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.state.GameMapState

class SelectorMouseProcessorV2(
    override val view: Container,
    val engine: Engine,
    val uiWorld: World,
    var isEnabled: Boolean = true
) :
    MouseComponent, EComponent {

    val gameMapState = uiWorld.injections.getSingleton<GameMapState>()

    val selectionRectangle = view.solidRect(0, 0, Colors.BLUE).alpha(0.25).visible(false)

    var dragging = false
    var isInitialClick = false

    var startPosition = Point()
    var currentPosition = Point()

    var previousSelectionSnapshot = emptyList<UIEntity>()

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (!isEnabled) return
        if (event.type == MouseEvent.Type.MOVE) return
        println("""
            selectorMouseComponent:
            event: $event
        """.trimIndent())

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
            dragging = false
            isInitialClick = false
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
                gameMapState.getIntersectingEntities(selectionRectangle.getGlobalBounds())
            println("intersectingEntities: $intersectingEntities")

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
                engine.eventBus.send(
                    SelectedUIEntitiesEvent(
                        SelectionType.SELECTED,
                        previousSelectionSnapshot,
                        intersectingEntities
                    )
                )
            } else {
                engine.eventBus.send(
                    SelectedUIEntitiesEvent(
                        SelectionType.PRE_SELECTION,
                        previousSelectionSnapshot,
                        intersectingEntities
                    )
                )
            }
            previousSelectionSnapshot = intersectingEntities
        }

    }

}