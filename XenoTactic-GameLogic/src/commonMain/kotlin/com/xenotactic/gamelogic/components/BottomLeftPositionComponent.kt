package com.xenotactic.gamelogic.components

import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.serialization.SerializableComponentI
import com.xenotactic.gamelogic.serialization.SerializableComponentI2
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.Serializable

// Component representing the bottom left position of an entity.
@Serializable
data class BottomLeftPositionComponent(
    val x: GameUnit, val y: GameUnit
) : SerializableComponentI<BottomLeftPositionComponent>, SerializableComponentI2 {
    fun toTuple() = GameUnitTuple(x, y)

    constructor(x: Int, y: Int): this(x.toGameUnit(), y.toGameUnit())

    override val klassName: String
        get() = BottomLeftPositionComponent::class.qualifiedName!!
    override val data: BottomLeftPositionComponent
        get() = this
}