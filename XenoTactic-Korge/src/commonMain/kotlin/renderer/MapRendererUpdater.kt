package renderer

import components.GameMapComponent
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
    val gameMapComponent = engine.getOneTimeComponent<GameMapComponent>()

    init {
        eventBus.register<AddEntityEvent> {
            renderer.addEntity(it.entity)
        }
        eventBus.register<RemovedEntityEvent> {
            renderer.handleRemoveEntityEvent(it)
        }
        eventBus.register<UpdatedPathLengthEvent> {
            renderer.renderPathLines(gameMapComponent.shortestPath)
        }
    }
}