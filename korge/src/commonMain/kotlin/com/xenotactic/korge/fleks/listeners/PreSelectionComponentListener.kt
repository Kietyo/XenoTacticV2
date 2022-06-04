package com.xenotactic.korge.fleks.listeners

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Inject
import com.xenotactic.korge.fleks.components.EntityRenderComponent
import com.xenotactic.korge.fleks.components.PreSelectionComponent

class PreSelectionComponentListener : ComponentListener<PreSelectionComponent> {

    val entityRenderComponentMapper = Inject.componentMapper<EntityRenderComponent>()

    override fun onComponentAdded(entity: Entity, component: PreSelectionComponent) {
        TODO("Not yet implemented")
    }

    override fun onComponentRemoved(entity: Entity, component: PreSelectionComponent) {
        TODO("Not yet implemented")
    }

}