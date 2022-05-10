package components

import engine.Component
import events.EventBus
import input_processors.PointerAction

class ObjectPlacementComponent : Component {
    var pointerAction: PointerAction = PointerAction.Inactive
}