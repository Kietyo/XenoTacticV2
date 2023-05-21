package com.xenotactic.korge.ui

import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.state.GameMapDimensionsState
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.size
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.PlacedEntityEvent
import com.xenotactic.korge.state.EditorState
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.onClick
import korlibs.korge.ui.*
import korlibs.korge.view.Container
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.alignBottomToBottomOf
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.align.centerXOn

@OptIn(KorgeExperimental::class)
class UIEditorButtonsV2(
    val engine: Engine,
    val baseView: SContainer
) : Container() {
    private val uiMapV2 = engine.injections.getSingleton<UIMapV2>()
    private val gameMapDimensionsState = engine.stateInjections.getSingleton<GameMapDimensionsState>()
    private val editorState = engine.stateInjections.getSingleton<EditorState>()
    private val mouseDragInputProcessor = engine.injections.getSingleton<MouseDragInputProcessor>()

    init {
        val buttonStack = uiHorizontalStack {
            val addStartButton = uiButton(label = "Add Start") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.START) { // Switching to playing mode
                        editorState.switchToPlayingMode()
                    } else { // Switch to editing mode
                        editorState.switchToEditingMode(MapEntityType.START)
                    }
                }
            }
            val addFinishButton = uiButton(label = "Add Finish") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        editorState.switchToPlayingMode()
                    } else { // Switch to editing mode
                        editorState.switchToEditingMode(MapEntityType.FINISH)
                    }
                }
            }
            val addCheckpoint = uiButton(label = "Add Checkpoint") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        editorState.switchToPlayingMode()
                    } else { // Switch to editing mode
                        editorState.switchToEditingMode(MapEntityType.CHECKPOINT)
                    }
                }
            }
            val addTeleport = uiButton(label = "Add Teleport") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        editorState.switchToPlayingMode()
                    } else { // Switch to editing mode
                        editorState.switchToEditingMode(MapEntityType.TELEPORT_IN)
                    }
                }
            }
            uiButton(label = "Add rocks") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.ROCK) { // Switching to playing mode
                        editorState.switchToPlayingMode()
                    } else { // Switch to editing mode
                        editorState.switchToEditingMode(MapEntityType.ROCK)
                    }
                }
            }
            uiButton("Resize map") {
                onClick {
                    mouseDragInputProcessor.adjustSettings {
                        isEnabled = false
                    }
                    baseView.uiWindow("Resize Map", 150.0 size 150.0) {
                        val thisWindow = it
                        uiVerticalStack {
                            uiText("Width:")
                            val widthInput = uiTextInput(uiMapV2.mapWidth.toString())
                            uiText("Height:")
                            val heightInput = uiTextInput(uiMapV2.mapHeight.toString())
                            uiButton("Apply") {
                                onClick {
                                    thisWindow.close()
                                    mouseDragInputProcessor.adjustSettings {
                                        isEnabled = true
                                    }
                                    gameMapDimensionsState.changeDimensions(
                                        widthInput.text.toInt().toGameUnit(),
                                        heightInput.text.toInt().toGameUnit()
                                    )
                                }
                            }
                            centerXOn(it)
                        }
                        it.centerOn(baseView)
                    }
                }
            }
            engine.eventBus.register<PlacedEntityEvent> {
                when (it.entityType) {
                    MapEntityType.START -> addStartButton.disable()
                    MapEntityType.FINISH -> addFinishButton.disable()
                    MapEntityType.CHECKPOINT -> Unit
                    MapEntityType.ROCK -> TODO()
                    MapEntityType.TOWER -> TODO()
                    MapEntityType.TELEPORT_IN -> TODO()
                    MapEntityType.TELEPORT_OUT -> Unit
                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                    MapEntityType.MONSTER -> TODO()
                    MapEntityType.SUPPLY_DEPOT -> TODO()
                }
                editorState.switchToPlayingMode()
            }
        }

        engine.eventBus.register<EscapeButtonActionEvent> {
            editorState.switchToPlayingMode()
        }

        val deleteEntitiesButton = UIButton(text = "Delete entities")

        engine.eventBus.register<EntitySelectionChangedEvent> {
            println("EntitySelectionChangedEvent: Went in here?! ${engine.gameWorld.selectionFamily.getList()}")
            if (engine.gameWorld.selectionFamily.getList().isEmpty()) {
                deleteEntitiesButton.removeFromParent()
            } else {
                buttonStack.addChild(deleteEntitiesButton)
            }

            resize()
        }
    }

    fun resize() {
        centerXOn(this.baseView)
        alignBottomToBottomOf(this.baseView)
        //        alignBottomToBottomOfWindow()
    }

}