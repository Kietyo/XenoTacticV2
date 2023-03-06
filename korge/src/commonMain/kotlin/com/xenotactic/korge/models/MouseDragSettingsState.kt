package com.xenotactic.korge.models

import com.xenotactic.gamelogic.engine.State
import com.xenotactic.korge.input_processors.MouseDragStateSettings

class MouseDragSettingsState: State {
    val mouseDragStateSettings: MouseDragStateSettings = MouseDragStateSettings(
        allowLeftClickDragging = false
    )
}