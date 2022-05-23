package bridges

import com.soywiz.korge.view.SolidRect
import com.xenotactic.gamelogic.model.GameMap

class MapBridge(
    var gameMap: GameMap = GameMap.create(10, 10),
)