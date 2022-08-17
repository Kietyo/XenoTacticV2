package com.xenotactic.korge.ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.Container
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.ecomponents.EditorEComponent
import com.xenotactic.korge.ecomponents.GameMapControllerEComponent
import com.xenotactic.korge.ecomponents.UIMapEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.korge.input_processors.MouseDragState
import com.xenotactic.korge.input_processors.PlacedEntityEvent
import com.xenotactic.korge.input_processors.SelectorMouseProcessor

class UIEditorButtons(val engine: Engine) : Container() {
    val editorComponent = engine.getOneTimeComponent<EditorEComponent>()
    val uiMapComponent = engine.getOneTimeComponent<UIMapEComponent>()
    val mouseDragState = engine.getOneTimeComponent<MouseDragState>()
    val gameMapControllerEComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()
    val selectorMouseProcessor = engine.getOneTimeComponent<SelectorMouseProcessor>()

    val uiMap = uiMapComponent.uiMap

    val DEFAULT_NOTIFICATION_TEXT = "N/A"

    init {
        uiHorizontalStack {
            val addStartButton = uiButton(text = "Add Start") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.START) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.START)
                    }
                }
            }
            val addFinishButton = uiButton(text = "Add Finish") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.FINISH)
                    }
                }
            }
            val addCheckpoint = uiButton(text = "Add Checkpoint") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.CHECKPOINT)
                    }
                }
            }
            val addTeleport = uiButton(text = "Add Teleport") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.TELEPORT_IN)
                    }
                }
            }
            uiButton(text = "Add rocks") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.ROCK) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.ROCK)
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
                }
                switchToPlayingMode()
            }

            engine.eventBus.register<EscapeButtonActionEvent> {
                switchToPlayingMode()
            }
        }
    }

    fun switchToPlayingMode() {
        engine.eventBus.send(NotificationTextUpdateEvent(DEFAULT_NOTIFICATION_TEXT))
        mouseDragState.adjustSettings {
            allowLeftClickDragging = true
        }
        editorComponent.isEditingEnabled = false
        uiMap.hideHighlightRectangle()
        uiMap.clearHighlightLayer()
        selectorMouseProcessor.isEnabled = true
        println("selectorMouseProcessor is enabled")
    }

    fun switchToEditingMode(entityType: MapEntityType) {
        engine.eventBus.send(NotificationTextUpdateEvent(gameMapControllerEComponent.getNotificationText(entityType)))
        mouseDragState.adjustSettings {
            allowLeftClickDragging = false
        }
        editorComponent.isEditingEnabled = true
        editorComponent.entityTypeToPlace = entityType
        selectorMouseProcessor.isEnabled = false
    }

}