package com.xenotactic.korge.family_listeners

import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.FamilyListener
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.components.UIMapEntityComponent
import com.xenotactic.gamelogic.korge_utils.xy
import com.xenotactic.korge.ui.UIMapV2

class SetInitialPositionFamilyListener(
    val world: World
) : FamilyListener {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            UIMapEntityComponent::class,
            BottomLeftPositionComponent::class,
            SizeComponent::class
        )
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()

    override fun onAdd(entityId: EntityId) {
        val uiMapEntityComponent = world[entityId, UIMapEntityComponent::class]
        val bottomLeftPositionComponent = world[entityId, BottomLeftPositionComponent::class]
        val sizeComponent = world[entityId, SizeComponent::class]

        val (worldX, worldY) = uiMapV2.getWorldCoordinates(
            bottomLeftPositionComponent.x, bottomLeftPositionComponent.y, sizeComponent.height
        )

        println("""
            entityId: $entityId, bottomLeftPositionComponent: $bottomLeftPositionComponent, sizeComponent: $sizeComponent, worldX: $worldX, worldY: $worldY 
        """.trimIndent())

        uiMapEntityComponent.entityView.xy(worldX, worldY)
    }

    override fun onRemove(entityId: EntityId) {
        TODO("Not yet implemented")
    }

    override fun onExisting(entityId: EntityId) {
        TODO("Not yet implemented")
    }
}