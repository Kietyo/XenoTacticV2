package com.xenotactic.korge.fleks.listeners

import com.xenotactic.gamelogic.ecs.ComponentListener
import com.xenotactic.gamelogic.ecs.Entity
import com.xenotactic.korge.components.UIMapEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.fleks.components.EntityUIComponent
import com.xenotactic.korge.ui.UIMap

class RenderEntityComponentListener(
    val engine: Engine
) : ComponentListener<EntityRenderComponent> {
    val uiMap = engine.getOneTimeComponent<UIMapEComponent>().uiMap

    override fun onAdd(entity: Entity, component: EntityRenderComponent) {
        entity.addOrReplaceComponent(EntityUIComponent(
            uiMap.addEntity(component.entity)
        ))
    }

    override fun onRemove(entity: Entity, component: EntityRenderComponent) {
        TODO("Not yet implemented")
    }

}