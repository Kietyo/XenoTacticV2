package com.xenotactic.korge.input_processors

import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GameMapApi
import com.xenotactic.korge.state.DeadUIZonesState
import korlibs.event.EventListener
import korlibs.event.MouseButton
import korlibs.event.MouseEvent
import korlibs.image.color.Colors
import korlibs.korge.view.*
import korlibs.math.geom.Point

class SelectorMouseProcessorV2(
    val views: Views,
    val view: Container,
    val engine: Engine,
    var isEnabled: Boolean = true
) {

    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val deadUIZonesState = engine.stateInjections.getSingleton<DeadUIZonesState>()

    private val selectionRectangle = view.solidRect(0, 0, Colors.BLUE).alpha(0.25).visible(false)

    private var dragging = false
    private var isInitialClick = false

    private var startPosition = Point()
    private var currentPosition = Point()

    fun reset() {
        dragging = false
        isInitialClick = false
    }

    fun setup(eventListener: EventListener) {
        eventListener.onEvents(*MouseEvent.Type.ALL) {
            onMouseEvent(it)
        }
    }

    fun onMouseEvent(event: MouseEvent) {
        if (!isEnabled) return
        if (event.type == MouseEvent.Type.MOVE) return

        if (event.type == MouseEvent.Type.DOWN &&
            event.button == MouseButton.LEFT
        ) {
            dragging = true
            isInitialClick = true
            startPosition = views.globalMousePos
        }

        if (event.type == MouseEvent.Type.CLICK &&
            event.button == MouseButton.LEFT
        ) {
            reset()
            return
        }

        currentPosition = views.globalMousePos

        if (deadUIZonesState.zones.any {
                if (it.hitTestAny(currentPosition)) {
                    println("Hit view: $it")
                    true
                } else {
                    false
                }
            }) {
            return
        }



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
                scaledWidth = (currentPosition.x - startPosition.x)
                scaledHeight = (currentPosition.y - startPosition.y)
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