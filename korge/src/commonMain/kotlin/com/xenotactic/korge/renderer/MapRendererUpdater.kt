package com.xenotactic.korge.renderer

import com.xenotactic.korge.ecomponents.GameMapControllerEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.RemovedEntityEvent
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.ui.UIMap

class MapRendererUpdater(
    val engine: Engine,
    val uiMap: UIMap,
    val eventBus: EventBus
) {
    val gameMapControllerComponent = engine.injections.getSingleton<GameMapControllerEComponent>()

    init {
        eventBus.register<AddEntityEvent> { event ->
            engine.gameWorld.addEntity {
                addOrReplaceComponent(EntityRenderComponent(event.entity))
            }
        }
        eventBus.register<RemovedEntityEvent> {
            TODO()
//            renderer.handleRemoveEntityEvent(it)
        }
        eventBus.register<UpdatedPathLineEvent> {
//            TODO()
            uiMap.renderPathLines(gameMapControllerComponent.shortestPath)
        }
    }
}