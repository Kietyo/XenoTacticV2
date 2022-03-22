package ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.UIVerticalStack
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korge.ui.uiWindow
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.alignBottomToBottomOf
import com.soywiz.korge.view.centerOnStage
import components.GameMapComponent
import components.ObjectPlacementComponent
import engine.Engine
import events.*
import com.xenotactic.gamelogic.model.MapEntity
import input_processors.PointerAction
import solver.*
import korge_utils.alignRightToRightOfWindow
import korge_utils.getReferenceParent
import korge_utils.onStageResizedV2

inline fun Container.uiPlacement(engine: Engine, eventBus: EventBus): UIPlacement =
    UIPlacement(engine, eventBus).addTo(this)

class UIPlacement(
    val engine: Engine,
    val eventBus: EventBus
) : Container() {
    val placementComponent = engine.getOneTimeComponent<ObjectPlacementComponent>()
    val placementContainer: UIVerticalStack

    init {

        placementContainer = uiVerticalStack {
            uiButton {
                text = "Tower"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.HighlightForPlacement(MapEntity.Tower(0, 0))
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Small Blocker"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.HighlightForPlacement(MapEntity.SmallBlocker(0, 0))
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Rock"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.HighlightForPlacement(MapEntity.ROCK_4X2)
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Remove Tower"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.RemoveTowerAtPlace()
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Remove Rock"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.RemoveRockAtPlace()
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Spawn creep"
                onClick {
                    logger.debug { "Spawn creep button clicked!" }
                    eventBus.send(SpawnCreepEvent)
                }
            }
            uiButton {
                text = "Debug"
                onClick {
                    val window = parent!!.uiWindow("Debug Window") {
                        uiButton {
                            text = "Solve"
                            onClick {
                                val solver = StandardSolver3(SolverSettings())
                                val mapComponent = engine.getOneTimeComponent<GameMapComponent>()
                                val solution = solver.solve(
                                    mapComponent.getGameMapDebugOnly(),
                                    SolverParams(5, OptimizationGoal.MaxPath)
                                )
                                when (solution) {
                                    SolverResult.Unsuccessful -> Unit
                                    is SolverResult.Success -> {
                                        solution.searchResult.state.towerPlacements.forEach {
                                            mapComponent.placeEntity(MapEntity.Tower(it.x, it.y))
                                        }
                                    }
                                }
                            }
                        }
                    }.centerOnStage()
                }
            }
            uiButton {
                text = "Exit"
                onClick {
                    eventBus.send(ExitGameSceneEvent)
                }
            }
        }

//        onStageResizedV2(true) { width, height ->
//            logger.debug {
//                "stage resized? width: $width, height: $height"
//            }
//
//            placementContainer.alignRightToRightOfWindow()
////            placementContainer.alignBottomToBottomOfWindow(width, height)
//            placementContainer.alignBottomToBottomOf(placementContainer.getReferenceParent())
//        }

        eventBus.register<EscapeButtonActionEvent> {
            handleEscapeAction()
        }
    }

    fun handleEscapeAction() {
        placementComponent.pointerAction = PointerAction.Inactive
        afterPointerActionChange()
    }

    private fun afterPointerActionChange() {
        eventBus.send(PointerActionChangeEvent)
    }

    companion object {
        val logger = Logger<UIPlacement>()
    }
}