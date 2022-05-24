package ui

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.Container
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import components.EditorEComponent
import components.NotificationTextEComponent
import components.UIMapEComponent
import engine.Engine
import input_processors.MouseDragKomponent

class UIEditorButtons(
    val engine: Engine
) : Container() {
    val editorComponent = engine.getOneTimeComponent<EditorEComponent>()
    val uiMapComponent = engine.getOneTimeComponent<UIMapEComponent>()
    val mouseDragKomponent = engine.getOneTimeComponent<MouseDragKomponent>()
    val notificationTextEComponent = engine.getOneTimeComponent<NotificationTextEComponent>()

    val uiMap = uiMapComponent.uiMap
    val notificationText = notificationTextEComponent.text

    val DEFAULT_NOTIFICATION_TEXT = "N/A"

    init {
        uiHorizontalStack {
            uiButton(text = "Add Start") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.START) { // Switching to playing mode
                        notificationText.text = DEFAULT_NOTIFICATION_TEXT
                        mouseDragKomponent.adjustSettings {
                            allowLeftClickDragging = true
                        }
                        editorComponent.isEditingEnabled = false
                        uiMap.hideHighlightRectangle()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.START)
                    }
                }
            }
            uiButton(text = "Add rocks") {
                onClick {
                    if (editorComponent.isEditingEnabled && editorComponent.entityTypeToPlace == MapEntityType.ROCK) { // Switching to playing mode
                        notificationText.text = "N/A"
                        mouseDragKomponent.adjustSettings {
                            allowLeftClickDragging = true
                        }
                        editorComponent.isEditingEnabled = false
                        uiMap.hideHighlightRectangle()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.ROCK)
                    }
                }
            }
        }
    }

    fun switchToEditingMode(entityType: MapEntityType) {
        notificationText.text = getNotificationText(entityType)
        mouseDragKomponent.adjustSettings {
            allowLeftClickDragging = false
        }
        editorComponent.isEditingEnabled = true
        editorComponent.entityTypeToPlace = entityType
    }

    fun getNotificationText(entityType: MapEntityType): String {
        val entityName = when (entityType) {
            MapEntityType.START -> "Start"
            MapEntityType.FINISH -> TODO()
            MapEntityType.CHECKPOINT -> TODO()
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