package com.xenotactic.korge.listeners_component

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.UIEntityViewComponent

class UIMapEntityComponentListener : ComponentListener<UIEntityViewComponent> {
    override fun onRemove(entityId: EntityId, component: UIEntityViewComponent) {
        component.entityView.removeFromParent()
    }

}