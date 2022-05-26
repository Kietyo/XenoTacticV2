package ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.centerXOnStage
import com.soywiz.korge.view.text
import com.xenotactic.gamelogic.model.MapEntityType
import components.EditorEComponent
import components.GameMapControllerEComponent
import components.NotificationTextEComponent
import components.UIMapEComponent
import engine.Engine
import events.EscapeButtonActionEvent
import input_processors.MouseDragKomponent
import input_processors.PlacedEntityEvent

class UIEditorButtons(
    val engine: Engine
) : Container() {
    val editorComponent = engine.getOneTimeComponent<EditorEComponent>()
    val uiMapComponent = engine.getOneTimeComponent<UIMapEComponent>()
    val mouseDragKomponent = engine.getOneTimeComponent<MouseDragKomponent>()
    val gameMapControllerEComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()

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
                    MapEntityType.TELEPORT_OUT -> TODO()
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
        engine.eventBus.send(NotificationTextUpdateEvent(
            DEFAULT_NOTIFICATION_TEXT
        ))
        mouseDragKomponent.adjustSettings {
            allowLeftClickDragging = true
        }
        editorComponent.isEditingEnabled = false
        uiMap.hideHighlightRectangle()
    }

    fun switchToEditingMode(entityType: MapEntityType) {
        engine.eventBus.send(NotificationTextUpdateEvent(
            getNotificationText(entityType)
        ))
        mouseDragKomponent.adjustSettings {
            allowLeftClickDragging = false
        }
        editorComponent.isEditingEnabled = true
        editorComponent.entityTypeToPlace = entityType
    }

    fun getNotificationText(entityType: MapEntityType): String {
        val entityName = when (entityType) {
            MapEntityType.START -> "Start"
            MapEntityType.FINISH -> "Finish"
            MapEntityType.CHECKPOINT -> "Checkpoint ${gameMapControllerEComponent.numCheckpoints + 1}"
            MapEntityType.ROCK -> "Rock"
            MapEntityType.TOWER -> TODO()
            MapEntityType.TELEPORT_IN -> TODO()
            MapEntityType.TELEPORT_OUT -> TODO()
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
        }

        return "Placement Mode: $entityName"
    }
}