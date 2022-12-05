package com.xenotactic.korge.input_processors

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GameUnit

data class RemoveEntityData(val x: GameUnit, val y: GameUnit, val entity: MapEntity)

sealed class PointerAction {
    object Inactive : PointerAction()
    data class HighlightForPlacement(
        val mapEntity: MapEntity, var placementLocation: GameUnitTuple? =
            null
    ) : PointerAction()

    data class RemoveEntityAtPlace(
        val entityType: MapEntityType,
        var data: RemoveEntityData? = null) : PointerAction()
}
