package com.xenotactic.korge.component_listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.UIMapEntityComponent

class UIMapEntityComponentListener : ComponentListener<UIMapEntityComponent> {
    override fun onAdd(entityId: EntityId, component: UIMapEntityComponent) {
    }

    override fun onRemove(entityId: EntityId, component: UIMapEntityComponent) {
        component.entityView.removeFromParent()
    }

    override fun onExisting(entityId: EntityId, component: UIMapEntityComponent) {
        TODO("Not yet implemented")
    }

}