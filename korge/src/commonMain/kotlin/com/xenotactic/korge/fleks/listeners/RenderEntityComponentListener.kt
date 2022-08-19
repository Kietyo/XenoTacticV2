package com.xenotactic.korge.fleks.listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.Entity
import com.xenotactic.ecs.World
import com.xenotactic.korge.ecomponents.UIMapEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.fleks.components.EntityUIComponent

class RenderEntityComponentListener(
    val world: World,
    val engine: Engine
) : ComponentListener<EntityRenderComponent> {
    val uiMap = engine.injections.getSingleton<UIMapEComponent>().uiMap
    val entityUIComponent = world.getComponentContainer<EntityUIComponent>()

    override fun onAdd(entity: Entity, component: EntityRenderComponent) {
        world.modifyEntity(entity) {
            addOrReplaceComponent(
                EntityUIComponent(
                    uiMap.addEntity(component.entity)
                )
            )
        }
    }

    override fun onRemove(entity: Entity, component: EntityRenderComponent) {
        TODO("Not yet implemented")
    }

}