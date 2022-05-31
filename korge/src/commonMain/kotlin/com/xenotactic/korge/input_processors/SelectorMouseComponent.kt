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
import com.xenotactic.korge.components.UIMapEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.fleks.components.SelectionType
import com.xenotactic.korge.ui.UIEntity

data class SelectedUIEntitiesEvent(
    val type: SelectionType,
    // Entities that were selected in the previous snapshot.
    val previousSelectionSnapshot: List<UIEntity>,
    // Entities that are selected in the newest snapshot.
    val newSelectionSnapshot: List<UIEntity>
)

class SelectorMouseComponent(
    override val view: Container,
    val engine: Engine
) :
    MouseComponent {

    val uiMap = engine.getOneTimeComponent<UIMapEComponent>().uiMap

    val selectionRectangle = view.solidRect(0, 0, Colors.BLUE).alpha(0.25).visible(false)

    var dragging = false
    var isInitialClick = false

    var startPosition = Point()
    var currentPosition = Point()

    var previousSelectionSnapshot = emptyList<UIEntity>()

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (event.type == MouseEvent.Type.MOVE) return
        println(event)

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
                uiMap.getIntersectingEntities(selectionRectangle.getGlobalBounds())
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