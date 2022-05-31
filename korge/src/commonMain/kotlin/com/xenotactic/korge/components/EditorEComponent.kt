package com.xenotactic.korge.components

import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.engine.EComponent

class EditorEComponent(
    var isEditingEnabled: Boolean = false,
    var entityTypeToPlace: MapEntityType = MapEntityType.ROCK
) : EComponent