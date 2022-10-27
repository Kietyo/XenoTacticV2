package com.xenotactic.korge.event_listeners

import com.soywiz.korge.ui.UIProgressBar
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.container
import com.soywiz.korma.geom.Anchor
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.components.*
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.korge_utils.makeEntityLabelText
import com.xenotactic.korge.ui.UIMapV2

interface TestEventListenerI<T> {
    fun handle(event: T)
}

data class AddedUIEntityEvent(
    val entityId: EntityId
)

data class AddedMonsterEntityEvent(
    val entityId: EntityId
)

class UIMapEventListeners(
    val engine: Engine
) {
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    val gameWorld = engine.gameWorld
    val world = gameWorld.world
    init {
        engine.eventBus.register<AddedUIEntityEvent> {
            handleAddedUIEntityEvent(it.entityId)
        }
        engine.eventBus.register<AddedMonsterEntityEvent> {
            handleAddedMonsterEntityEvent(it.entityId)
        }
    }

    private fun handleAddedUIEntityEvent(entityId: EntityId) {
        world.modifyEntity(entityId) {
            val mapEntityComponent = gameWorld.mapEntityComponent.getComponent(entityId)
            val sizeComponent = gameWorld.sizeComponent.getComponent(entityId)
            val uiEntity = createUiEntity(mapEntityComponent, sizeComponent)
            addComponentOrThrow(UIEntityViewComponent(uiEntity))

            val text = mapEntityComponent.entityData.getText()
            if (text != null) {
                val textView = makeEntityLabelText(text).apply {
                    addTo(uiEntity)
                    scaledHeight = uiMap.gridSize / 2
                    scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
                    centerOn(uiEntity)
                }
                addComponentOrThrow(UIMapEntityTextComponent(textView))
            }
        }
    }

    fun handleAddedMonsterEntityEvent(entityId: EntityId) {
        val sizeComponent = world[entityId, SizeComponent::class]
        val maxHealthComponent = world[entityId, MaxHealthComponent::class]
        val (worldWidth, worldHeight) = toWorldDimensions(sizeComponent.width, sizeComponent.height, uiMap.gridSize)
        val spriteContainer = uiMap.monsterLayer.container()
        val uiSprite = UIEightDirectionalSprite(GlobalResources.MONSTER_SPRITE).addTo(spriteContainer) {
            anchor(Anchor.CENTER)
            scaledWidth = worldWidth.toDouble()
            scaledHeight = worldHeight.toDouble()
        }
        val healthBar = createHealthBar(sizeComponent.width, maxHealthComponent.maxHealth).apply {
            addTo(spriteContainer)
        }
        world.modifyEntity(entityId) {
            addComponentOrThrow(UIEntityViewComponent(spriteContainer))
            addComponentOrThrow(UIEightDirectionalSpriteComponent(uiSprite))
            addComponentOrThrow(UIHealthBarComponent(healthBar))

        }
    }

    private fun createUiEntity(
        mapEntityComponent: MapEntityComponent,
        sizeComponent: SizeComponent
    ): UIEntity {
        val uiEntity = UIEntity(
            mapEntityComponent.entityData.toMapEntityType(),
            sizeComponent.width,
            sizeComponent.height,
            uiMap.gridSize,
            uiMap.borderSize,
            if (mapEntityComponent.entityData is MapEntityData.SpeedArea) (mapEntityComponent.entityData as MapEntityData.SpeedArea).speedEffect else null
        ).apply {
            when (mapEntityComponent.entityData) {
                is MapEntityData.SpeedArea -> addTo(uiMap.speedAreaLayer)
                is MapEntityData.Checkpoint,
                MapEntityData.Finish,
                MapEntityData.SmallBlocker,
                MapEntityData.Start,
                is MapEntityData.TeleportIn,
                is MapEntityData.TeleportOut,
                MapEntityData.Tower,
                MapEntityData.Rock -> addTo(uiMap.entityLayer)

                MapEntityData.Monster -> addTo(uiMap.monsterLayer)
            }
        }
        return uiEntity
    }

    private fun createHealthBar(
        diameterGameUnit: GameUnit,
        maxHealth: Double
    ): UIProgressBar {
        val diameter = diameterGameUnit.toWorldUnit(uiMap.gridSize)
        return UIProgressBar(
            diameter.toDouble(), diameter.toDouble() / 4.0,
            current = maxHealth, maximum = maxHealth
        ).apply {
            x -= diameter.toDouble() / 2.0
            y -= diameter.toDouble() / 5.0 * 4.0
        }
    }

}