package com.xenotactic.korge.family_listeners

import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.view.View
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.xy
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.FamilyListener
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.utils.*
import com.xenotactic.korge.korge_utils.makeEntityLabelText
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.ui.UIMapV2

class AddEntityFamilyListener(
    val world: World
) : FamilyListener {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MapEntityComponent::class, SizeComponent::class, BottomLeftPositionComponent::class
        ),
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()
    val mapEntityTypeContainer = world.getComponentContainer<MapEntityComponent>()
    val sizeComponentContainer = world.getComponentContainer<SizeComponent>()
    val bottomLeftPositionComponentContainer =
        world.getComponentContainer<BottomLeftPositionComponent>()

    override fun onAdd(entityId: EntityId) {
        val mapEntityComponent = mapEntityTypeContainer.getComponent(entityId)
        val sizeComponent = sizeComponentContainer.getComponent(entityId)
        println("onAdd. mapEntityComponent: $mapEntityComponent")

        val uiEntity = UIEntity(
            mapEntityComponent.entityData.toMapEntityType(),
            sizeComponent.width,
            sizeComponent.height,
            uiMapV2.gridSize,
            uiMapV2.borderSize,
            if (mapEntityComponent.entityData is MapEntityData.SpeedArea) (mapEntityComponent.entityData as MapEntityData.SpeedArea).speedEffect else null
        ).apply {
            if (mapEntityComponent.entityData is MapEntityData.SpeedArea) {
                addTo(uiMapV2.speedAreaLayer)
            } else {
                addTo(uiMapV2.entityLayer)
            }
        }

        val text = mapEntityComponent.entityData.getText()
        if (text != null) {
            val textView = makeEntityLabelText(text).apply {
                addTo(uiEntity)
                scaledHeight = uiMapV2.gridSize / 2
                scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
                centerOn(uiEntity)
            }
            world.modifyEntity(entityId) {
                addComponentOrThrow(UIMapEntityTextComponent(textView))
            }
        }

        world.modifyEntity(entityId) {
            addComponentOrThrow(UIMapEntityComponent(uiEntity))
        }

    }

    override fun onRemove(entityId: EntityId) {
    }

    override fun onExisting(entityId: EntityId) {
    }

}



