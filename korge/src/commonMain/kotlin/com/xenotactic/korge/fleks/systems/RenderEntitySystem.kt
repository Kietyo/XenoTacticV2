package com.xenotactic.korge.fleks.systems

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.xenotactic.korge.fleks.components.EntityRenderComponent

class RenderEntitySystem : IteratingSystem(
    allOfComponents = arrayOf(EntityRenderComponent::class)
) {
    override fun onTickEntity(entity: Entity) {
        return
    }
}