package com.xenotactic.korge.utils

import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.gamelogic.utils.Engine
import com.xenotactic.korge.components.GameMapControllerEComponent
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.RemovedEntityEvent
import com.xenotactic.korge.ui.UIMap

class MapRendererUpdater(
    val engine: Engine,
    val uiMap: UIMap,
    val eventBus: EventBus
) {
    val gameMapControllerComponent = engine.injections.getSingleton<GameMapControllerEComponent>()

    init {
        eventBus.register<AddEntityEvent> { event ->
            engine.gameWorld.world.addEntity {
                addOrReplaceComponent(com.xenotactic.gamelogic.components.EntityRenderComponent(event.entity))
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