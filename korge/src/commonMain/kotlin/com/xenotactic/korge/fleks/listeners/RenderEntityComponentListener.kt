package com.xenotactic.korge.fleks.listeners

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Inject
import com.github.quillraven.fleks.World
import com.xenotactic.gamelogic.ecs.ComponentListener
import com.xenotactic.gamelogic.ecs.Entity
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.fleks.components.EntityUIComponent
import com.xenotactic.korge.ui.UIMap

class RenderEntityComponentListener : ComponentListener<EntityRenderComponent> {
    val world = Inject.dependency<World>()
    val uiMap = Inject.dependency<UIMap>()
    val entityUIComponentMapper = Inject.componentMapper<EntityUIComponent>()

    override fun onComponentAdded(entity: Entity, component: EntityRenderComponent) {
        world.configureEntity(entity) {
            entityUIComponentMapper.add(it) {
                view = uiMap.addEntity(component.entity)
            }
        }
    }

    override fun onComponentRemoved(entity: Entity, component: EntityRenderComponent) {
        uiMap.removeEntity(component.entity)
    }

    override fun onAdd(entity: Entity, component: EntityRenderComponent) {
        TODO("Not yet implemented")
    }

    override fun onRemove(entity: Entity, component: EntityRenderComponent) {
        TODO("Not yet implemented")
    }

}