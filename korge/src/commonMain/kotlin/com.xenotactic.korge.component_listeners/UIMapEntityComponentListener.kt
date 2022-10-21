package com.xenotactic.korge.component_listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.korge.components.UIEntityViewComponent

class UIMapEntityComponentListener : ComponentListener<UIEntityViewComponent> {
    override fun onAdd(entityId: EntityId, component: UIEntityViewComponent) {
    }

    override fun onRemove(entityId: EntityId, component: UIEntityViewComponent) {
        component.entityView.removeFromParent()
    }

    override fun onExisting(entityId: EntityId, component: UIEntityViewComponent) {
        TODO("Not yet implemented")
    }

}