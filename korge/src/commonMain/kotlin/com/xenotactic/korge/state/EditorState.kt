package com.xenotactic.korge.state

import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.engine.EComponent

data class EditorState(
    var isEditingEnabled: Boolean = false,
    var entityTypeToPlace: MapEntityType = MapEntityType.ROCK
) : EComponent