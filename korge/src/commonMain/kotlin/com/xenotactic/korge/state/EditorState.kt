package com.xenotactic.korge.state

import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.gamelogic.utils.GameMapApi
import com.xenotactic.gamelogic.utils.State
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.SelectorMouseProcessorV2
import com.xenotactic.korge.ui.NotificationTextUpdateEvent
import com.xenotactic.korge.ui.UIMapV2

class EditorState(
    val engine: Engine,
    var isEditingEnabled: Boolean = false,
    var entityTypeToPlace: MapEntityType = MapEntityType.ROCK
) : State {
    private val DEFAULT_NOTIFICATION_TEXT = "N/A"

    private val uiMapV2 = engine.injections.getSingleton<UIMapV2>()
    private val mouseDragInputProcessor = engine.injections.getSingleton<MouseDragInputProcessor>()
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val selectorMouseProcessor = engine.injections.getSingleton<SelectorMouseProcessorV2>()

    fun toggle(entityType: MapEntityType) {
        if (isEditingEnabled) {
            switchToPlayingMode()
        } else {
            switchToEditingMode(entityType)
        }
    }

    fun switchToPlayingMode() {
        engine.eventBus.send(NotificationTextUpdateEvent(DEFAULT_NOTIFICATION_TEXT))
        mouseDragInputProcessor.adjustSettings {
            isEnabled = true
        }
        isEditingEnabled = false
        uiMapV2.hideHighlightRectangle()
        uiMapV2.clearHighlightLayer()
        selectorMouseProcessor.isEnabled = true
        println("selectorMouseProcessor is enabled")
    }

    fun switchToEditingMode(entityType: MapEntityType) {
        engine.eventBus.send(
            NotificationTextUpdateEvent(
                gameMapApi.getNotificationText(
                    entityType
                )
            )
        )
        mouseDragInputProcessor.adjustSettings {
            allowLeftClickDragging = false
        }
        isEditingEnabled = true
        entityTypeToPlace = entityType
        selectorMouseProcessor.isEnabled = false
        selectorMouseProcessor.reset()
    }
}