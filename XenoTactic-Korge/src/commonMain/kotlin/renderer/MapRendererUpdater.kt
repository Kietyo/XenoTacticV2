package renderer

import components.GameMapControllerEComponent
import engine.Engine
import events.AddEntityEvent
import events.EventBus
import events.RemovedEntityEvent
import events.UpdatedPathLengthEvent
import ui.UIMap

class MapRendererUpdater(
    val engine: Engine,
    val renderer: UIMap,
    val eventBus: EventBus
) {
    val gameMapControllerComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()

    init {
        eventBus.register<AddEntityEvent> {
            renderer.addEntity(it.entity)
        }
        eventBus.register<RemovedEntityEvent> {
            renderer.handleRemoveEntityEvent(it)
        }
        eventBus.register<UpdatedPathLengthEvent> {
            renderer.renderPathLines(gameMapControllerComponent.shortestPath)
        }
    }
}