package com.xenotactic.korge.component_listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.UIEntityViewComponent

class UIMapEntityComponentListener : ComponentListener<com.xenotactic.gamelogic.components.UIEntityViewComponent> {
    override fun onAdd(entityId: EntityId, component: com.xenotactic.gamelogic.components.UIEntityViewComponent) {
    }

    override fun onRemove(entityId: EntityId, component: com.xenotactic.gamelogic.components.UIEntityViewComponent) {
        component.entityView.removeFromParent()
    }

    override fun onExisting(entityId: EntityId, component: com.xenotactic.gamelogic.components.UIEntityViewComponent) {
        TODO("Not yet implemented")
    }

}