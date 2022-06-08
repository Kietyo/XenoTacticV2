package com.xenotactic.korge.renderer

import com.xenotactic.korge.components.GameMapControllerEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.RemovedEntityEvent
import com.xenotactic.korge.events.UpdatedPathLengthEvent
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.ui.UIMap

class MapRendererUpdater(
    val engine: Engine,
    val renderer: UIMap,
    val eventBus: EventBus
) {
    val gameMapControllerComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()

    init {
        eventBus.register<AddEntityEvent> { event ->
            engine.world.addEntity {
                addOrReplaceComponent(EntityRenderComponent(event.entity))
            }
        }
        eventBus.register<RemovedEntityEvent> {
            TODO()
//            renderer.handleRemoveEntityEvent(it)
        }
        eventBus.register<UpdatedPathLengthEvent> {
            TODO()
            renderer.renderPathLines(gameMapControllerComponent.shortestPath)
        }
    }
}