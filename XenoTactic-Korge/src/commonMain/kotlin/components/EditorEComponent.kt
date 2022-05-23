package components

import com.xenotactic.gamelogic.model.MapEntityType
import engine.EComponent

class EditorEComponent(
    var isEditingEnabled: Boolean = false,
    var entityTypeToPlace: MapEntityType = MapEntityType.ROCK
) : EComponent {
}