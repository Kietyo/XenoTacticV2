package com.xenotactic.korge.components

import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.korge.engine.EComponent

data class GameMapEComponent(
    val gameMap: GameMap
): EComponent
