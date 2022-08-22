package com.xenotactic.korge.fleks.listeners

import com.xenotactic.ecs.ComponentListener
import com.xenotactic.ecs.EntityId
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

    override fun onAdd(entityId: EntityId, component: EntityRenderComponent) {
        world.modifyEntity(entityId) {
            addOrReplaceComponent(
                EntityUIComponent(
                    uiMap.addEntity(component.entity)
                )
            )
        }
    }

    override fun onRemove(entityId: EntityId, component: EntityRenderComponent) {
        TODO("Not yet implemented")
    }

    override fun onExisting(entityId: EntityId, component: EntityRenderComponent) {
        TODO()
    }

}