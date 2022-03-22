package ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.alignBottomToBottomOf
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korio.async.AsyncSignal
import com.soywiz.korio.async.Signal
import com.soywiz.korio.async.Signal2
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

enum class UIPlacementButton {
    VIEW_ROCK_COUNTERS
}

class UIPlacement(
    val engine: Engine,
    val eventBus: EventBus
) : Container() {
    val placementComponent = engine.getOneTimeComponent<ObjectPlacementComponent>()
    val placementContainer: UIVerticalStack

    val onButtonClick = AsyncSignal<UIPlacementButton>()

    init {

        placementContainer = uiVerticalStack {
            uiButton {
                uiSkin = UISkin {
                    setSkinProperty("textSize", 12.0)
                }
                text = "View rock counters"
                onClick {
                    onButtonClick(UIPlacementButton.VIEW_ROCK_COUNTERS)
                }
            }
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