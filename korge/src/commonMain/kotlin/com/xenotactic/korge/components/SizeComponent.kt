package com.xenotactic.korge.components

import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.toGameUnit

data class SizeComponent(
    val width: GameUnit,
    val height: GameUnit
) {
    fun toRadius(): GameUnit {
        require(width == height)
        return width / 2.0
    }
    companion object {
        val SIZE_2X2_COMPONENT = SizeComponent(2.toGameUnit(), 2.toGameUnit())
    }
}