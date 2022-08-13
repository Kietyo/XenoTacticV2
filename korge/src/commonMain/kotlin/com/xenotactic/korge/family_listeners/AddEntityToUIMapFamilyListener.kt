package com.xenotactic.korge.family_listeners

import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.xy
import com.xenotactic.ecs.Entity
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.FamilyListener
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.components.MapEntityComponent
import com.xenotactic.korge.components.SizeComponent
import com.xenotactic.korge.components.BottomLeftPositionComponent
import com.xenotactic.korge.components.SpeedEffectComponent
import com.xenotactic.korge.components.UIMapEntityComponent
import com.xenotactic.korge.ui.UIEntity
import com.xenotactic.korge.ui.UIMapV2

class AddEntityToUIMapFamilyListener(
    val world: World
) : FamilyListener {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MapEntityType::class, SizeComponent::class, BottomLeftPositionComponent::class
        ),
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()
    val mapEntityTypeContainer = world.getComponentContainer<MapEntityType>()
    val sizeComponentContainer = world.getComponentContainer<SizeComponent>()
    val bottomLeftPositionComponentContainer =
        world.getComponentContainer<BottomLeftPositionComponent>()
    val speedEffectComponentContainer = world.getComponentContainer<SpeedEffectComponent>()

    override fun onAdd(entity: Entity) {
        val mapEntityComponent = mapEntityTypeContainer.getComponent(entity)
        val bottomLeftPositionComponent = bottomLeftPositionComponentContainer.getComponent(entity)
        val sizeComponent = sizeComponentContainer.getComponent(entity)
        val speedEffectComponent = speedEffectComponentContainer.getComponentOrNull(entity)
        val (worldX, worldY) = uiMapV2.getWorldCoordinates(
            bottomLeftPositionComponent.x, bottomLeftPositionComponent.y, sizeComponent.height
        )

        println("onAdd. mapEntityComponent: $mapEntityComponent")

        val uiEntity = UIEntity(
            mapEntityComponent,
            sizeComponent.width,
            sizeComponent.height,
            uiMapV2.gridSize,
            uiMapV2.borderSize,
            if (mapEntityComponent == MapEntityType.SPEED_AREA) speedEffectComponent!!.speedEffect else null
        ).apply {
            if (mapEntityComponent == MapEntityType.SPEED_AREA) {
                addTo(uiMapV2.speedAreaLayer)
            } else {
                addTo(uiMapV2.entityLayer)
            }
            xy(worldX, worldY)
        }

        world.modifyEntity(entity) {
            addComponentOrThrow(UIMapEntityComponent(uiEntity))
        }

    }

    override fun onRemove(entity: Entity) {
    }

    override fun onExisting(entity: Entity) {
    }

}