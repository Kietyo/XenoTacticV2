package com.xenotactic.korge.component_listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.fleks.components.PreSelectionComponent

class PreSelectionComponentListener(
    val engine: Engine
) : ComponentListener<PreSelectionComponent> {
    override fun onAdd(entityId: EntityId, component: PreSelectionComponent) {
        val uiMapEntityComponent = engine.gameWorld.uiMapEntityComponentContainer.getComponent(entityId)
        uiMapEntityComponent.uiEntity.doInProcessSelection()
    }

    override fun onRemove(entityId: EntityId, component: PreSelectionComponent) {
        if (engine.gameWorld.selectionComponentContainer.containsComponent(entityId)) return
        val uiMapEntityComponent = engine.gameWorld.uiMapEntityComponentContainer.getComponent(entityId)
        uiMapEntityComponent.uiEntity.cancelSelection()
    }

    override fun onExisting(entityId: EntityId, component: PreSelectionComponent) {
        TODO("Not yet implemented")
    }

}