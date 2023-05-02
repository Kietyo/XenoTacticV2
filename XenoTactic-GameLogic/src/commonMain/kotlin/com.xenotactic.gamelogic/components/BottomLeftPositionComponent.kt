package com.xenotactic.gamelogic.components

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.SerializableComponentI2
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.Serializable

// Component representing the bottom left position of an entity.
@Serializable
data class BottomLeftPositionComponent(
    val x: GameUnit, val y: GameUnit
) : SerializableComponentI2 {
    fun toTuple() = GameUnitTuple(x, y)

    constructor(x: Int, y: Int): this(x.toGameUnit(), y.toGameUnit())
}