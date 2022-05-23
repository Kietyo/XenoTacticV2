package components

import com.xenotactic.gamelogic.model.GameMap
import engine.EComponent

data class GameMapEComponent(
    val gameMap: GameMap
): EComponent
