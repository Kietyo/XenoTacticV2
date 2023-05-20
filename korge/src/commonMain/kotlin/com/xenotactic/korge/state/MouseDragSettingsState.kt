package com.xenotactic.korge.state

import com.xenotactic.gamelogic.utils.State
import com.xenotactic.korge.input_processors.MouseDragStateSettings

class MouseDragSettingsState : State {
    val mouseDragStateSettings: MouseDragStateSettings = MouseDragStateSettings(
        allowLeftClickDragging = false
    )
}