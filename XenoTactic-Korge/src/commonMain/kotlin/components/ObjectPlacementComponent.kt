package components

import engine.Component
import events.EventBus
import input_processors.PointerAction

class ObjectPlacementComponent(val eventBus: EventBus) : Component {
    var pointerAction: PointerAction = PointerAction.Inactive
}