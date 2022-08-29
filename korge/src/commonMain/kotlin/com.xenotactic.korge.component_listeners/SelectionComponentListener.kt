package com.xenotactic.korge.component_listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.fleks.components.SelectedComponent

class SelectionComponentListener(
    val engine: Engine
) : ComponentListener<SelectedComponent> {
    override fun onAdd(entityId: EntityId, component: SelectedComponent) {
        val uiMapEntityComponent = engine.gameWorld.uiMapEntityComponentContainer.getComponent(entityId)
        uiMapEntityComponent.entityView.doActiveSelection()
        engine.eventBus.send(EntitySelectionChangedEvent)
    }

    override fun onRemove(entityId: EntityId, component: SelectedComponent) {
        println("Removed selection for entity: $entityId")
        val uiMapEntityComponent = engine.gameWorld.uiMapEntityComponentContainer.getComponent(entityId)
        uiMapEntityComponent.entityView.cancelSelection()
        engine.eventBus.send(EntitySelectionChangedEvent)
    }

    override fun onExisting(entityId: EntityId, component: SelectedComponent) {
        TODO("Not yet implemented")
    }
}