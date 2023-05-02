package com.xenotactic.gamelogic.components

import com.xenotactic.gamelogic.model.SerializableComponentI2
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlinx.serialization.Serializable

@Serializable
data class SizeComponent(
    val width: GameUnit,
    val height: GameUnit
): SerializableComponentI2 {
    constructor(width: Int, height: Int): this(width.toGameUnit(), height.toGameUnit())
    fun toRadius(): GameUnit {
        require(width == height)
        return width / 2.0
    }
    companion object {
        val SIZE_2X2_COMPONENT = com.xenotactic.gamelogic.components.SizeComponent(2.toGameUnit(), 2.toGameUnit())
    }
}