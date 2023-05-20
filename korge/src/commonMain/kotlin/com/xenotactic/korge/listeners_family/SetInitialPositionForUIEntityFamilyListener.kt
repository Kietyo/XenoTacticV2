package com.xenotactic.korge.listeners_family

import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.FamilyListener
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.components.UIEntityViewComponent
import com.xenotactic.gamelogic.utils.xy
import com.xenotactic.korge.ui.UIMapV2

class SetInitialPositionForUIEntityFamilyListener(
    val world: World
) : FamilyListener {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            UIEntityViewComponent::class,
            BottomLeftPositionComponent::class,
            SizeComponent::class
        )
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()

    override fun onAdd(entityId: EntityId) {
        val uiEntityViewComponent = world[entityId, UIEntityViewComponent::class]
        val bottomLeftPositionComponent = world[entityId, BottomLeftPositionComponent::class]
        val sizeComponent = world[entityId, SizeComponent::class]

        val (worldX, worldY) = uiMapV2.getWorldCoordinates(
            bottomLeftPositionComponent.x, bottomLeftPositionComponent.y, sizeComponent.height
        )

        println(
            """
            entityId: $entityId, bottomLeftPositionComponent: $bottomLeftPositionComponent, sizeComponent: $sizeComponent, worldX: $worldX, worldY: $worldY 
        """.trimIndent()
        )

        uiEntityViewComponent.entityView.xy(worldX, worldY)
    }

    override fun onRemove(entityId: EntityId) = Unit

    override fun onExisting(entityId: EntityId) {
        TODO("Not yet implemented")
    }
}