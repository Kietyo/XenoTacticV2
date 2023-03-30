package com.xenotactic.korge.ui

import korlibs.logger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.UIVerticalStack
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korge.ui.uiWindow
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.centerOnStage
import korlibs.io.async.AsyncSignal
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.ecomponents.GameMapControllerEComponent
import com.xenotactic.korge.ecomponents.ObjectPlacementEComponent
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.events.ExitGameSceneEvent
import com.xenotactic.korge.events.PointerActionChangeEvent
import com.xenotactic.korge.events.SpawnCreepEvent
import com.xenotactic.korge.input_processors.PointerAction
import solver.OptimizationGoal
import solver.SolverParams
import solver.SolverResult
import solver.SolverSettings
import solver.StandardSolver3

inline fun Container.uiPlacement(engine: Engine, eventBus: EventBus): UIPlacement =
    UIPlacement(engine, eventBus).addTo(this)

enum class UIPlacementButton {
    VIEW_ROCK_COUNTERS
}

class UIPlacement(
    val engine: Engine,
    val eventBus: EventBus
) : Container() {
    val placementComponent = engine.injections.getSingleton<ObjectPlacementEComponent>()
    val placementContainer: UIVerticalStack

    val onButtonClick = AsyncSignal<UIPlacementButton>()

    init {

        placementContainer = uiVerticalStack {
            uiButton {
                textSize = 12.0
                text = "View rock counters"
                onClick {
                    onButtonClick(UIPlacementButton.VIEW_ROCK_COUNTERS)
                }
            }
            uiButton {
                text = "Tower"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.HighlightForPlacement(MapEntity.Tower(0.toGameUnit(), 0.toGameUnit()))
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Small Blocker"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.HighlightForPlacement(MapEntity.SmallBlocker(0.toGameUnit(), 0.toGameUnit()))
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
                        PointerAction.RemoveEntityAtPlace(MapEntityType.TOWER)
                    afterPointerActionChange()
                }
            }
            uiButton {
                text = "Remove Rock"
                onClick {
                    placementComponent.pointerAction =
                        PointerAction.RemoveEntityAtPlace(MapEntityType.ROCK)
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
                                val mapComponent =
                                    engine.injections.getSingleton<GameMapControllerEComponent>()
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