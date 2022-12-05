package com.xenotactic.korge.component_listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
import com.xenotactic.korge.components.UIMapEntityTextComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.ui.UIMapV2

class UIMapEntityTextComponentListener(
    val engine: Engine
) : ComponentListener<UIMapEntityTextComponent> {
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    override fun onAdd(entityId: EntityId, component: UIMapEntityTextComponent) = Unit

    override fun onRemove(entityId: EntityId, component: UIMapEntityTextComponent) {
        uiMap.entityLabelLayer.removeChild(component.textView)
    }

    override fun onExisting(entityId: EntityId, component: UIMapEntityTextComponent) {
        TODO("Not yet implemented")
    }

}