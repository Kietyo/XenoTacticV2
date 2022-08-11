package com.xenotactic.korge.state

import com.xenotactic.gamelogic.model.MapEntityType

data class EditorState(
    var isEditingEnabled: Boolean = false,
    var entityTypeToPlace: MapEntityType = MapEntityType.ROCK
) {
}