package components

import engine.EComponent
import input_processors.PointerAction

class ObjectPlacementEComponent : EComponent {
    var pointerAction: PointerAction = PointerAction.Inactive
}