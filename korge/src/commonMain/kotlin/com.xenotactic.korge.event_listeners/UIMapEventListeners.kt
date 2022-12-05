package com.xenotactic.korge.event_listeners

import com.soywiz.korge.ui.UIProgressBar
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korma.geom.Anchor
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.GameUnit
import com.xenotactic.gamelogic.utils.GlobalResources
import com.xenotactic.gamelogic.utils.toWorldDimensions
import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import com.xenotactic.korge.components.*
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.korge_utils.getText
import com.xenotactic.korge.korge_utils.makeEntityLabelText
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.ui.UIMapV2

interface TestEventListenerI<T> {
    fun handle(event: T)
}

data class AddedUIEntityEvent(
    val entityId: EntityId
)

data class RemoveUIEntitiesEvent(
    val entities: Set<EntityId>
)


data class AddedMonsterEntityEvent(
    val entityId: EntityId
)

class UIMapEventListeners(
    val engine: Engine
) {
    val uiMap = engine.injections.getSingleton<UIMapV2>()
    val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    val gameWorld = engine.gameWorld
    val world = gameWorld.world

    init {
        engine.eventBus.register<AddedUIEntityEvent> {
            handleAddedUIEntityEvent(it.entityId)
        }
        engine.eventBus.register<RemoveUIEntitiesEvent> {
            handleRemoveUIEntitiesEvent(it.entities)
        }
        engine.eventBus.register<AddedMonsterEntityEvent> {
            handleAddedMonsterEntityEvent(it.entityId)
        }
    }

    private fun handleRemoveUIEntitiesEvent(entities: Set<EntityId>) {
        gameMapApi.removeEntities(entities)
    }

    private fun handleAddedUIEntityEvent(entityId: EntityId) {
        world.modifyEntity(entityId) {
            val entityTypeComponent = gameWorld.entityTypeComponents.getComponent(entityId)
            val sizeComponent = gameWorld.sizeComponent.getComponent(entityId)
            val uiEntityContainer = Container()

            val borderSize = uiMap.borderSize
            val (worldWidth, worldHeight) = toWorldDimensions(sizeComponent.width, sizeComponent.height, uiMap.gridSize)

            when (entityTypeComponent.type) {
                MapEntityType.CHECKPOINT -> {
                    Circle((worldWidth / 2).value, Colors.MAROON).addTo(uiEntityContainer)
                }

                MapEntityType.FINISH -> {
                    Circle((worldWidth / 2).value, Colors.MAGENTA).addTo(uiEntityContainer)
                }

                MapEntityType.START -> {
                    Circle((worldWidth / 2).value, Colors.RED).apply {
                        addTo(uiEntityContainer)
                    }
                }

                MapEntityType.TOWER -> {
                    uiEntityContainer.solidRect(
                        worldWidth.value, worldHeight.value,
                        MaterialColors.YELLOW_500
                    )
                    uiEntityContainer.solidRect(
                        (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                        MaterialColors.YELLOW_900
                    ).centerOn(uiEntityContainer)
                }

                MapEntityType.ROCK -> {
                    uiEntityContainer.solidRect(
                        worldWidth.value, worldHeight.value,
                        MaterialColors.BROWN_500
                    )
                    uiEntityContainer.solidRect(
                        (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                        MaterialColors.BROWN_900
                    ).centerOn(uiEntityContainer)
                }

                MapEntityType.TELEPORT_IN -> {
                    Circle(worldWidth.value / 2, Colors.GREEN.withAd(0.6)).addTo(uiEntityContainer)
                }

                MapEntityType.TELEPORT_OUT -> {
                    Circle(worldWidth.value / 2, Colors.RED.withAd(0.6)).addTo(uiEntityContainer)
                }

                MapEntityType.SMALL_BLOCKER -> {
                    uiEntityContainer.solidRect(
                        worldWidth.value, worldHeight.value,
                        MaterialColors.YELLOW_500
                    )
                    uiEntityContainer.solidRect(
                        (worldWidth - borderSize).value, (worldHeight - borderSize).value,
                        MaterialColors.YELLOW_900
                    ).centerOn(uiEntityContainer)
                }

                MapEntityType.SPEED_AREA -> {
                    val speedEffect = world[entityId, EntitySpeedAreaComponent::class].speedEffect
                    val speedAreaColor = com.xenotactic.gamelogic.korge_utils.SpeedAreaColorUtil(
                        speedEffect,
                        slowLow = 0.3, slowHigh = 0.9, fastLow = 1.2, fastHigh = 2.0
                    ).withAd(0.4)
                    Circle(worldWidth.value / 2, speedAreaColor).addTo(uiEntityContainer)
                }

                MapEntityType.MONSTER -> {
                    UIEightDirectionalSprite(GlobalResources.MONSTER_SPRITE).addTo(uiEntityContainer) {
                        anchor(Anchor.CENTER)
                        scaledWidth = worldWidth.toDouble()
                        scaledHeight = worldHeight.toDouble()
                    }
                }
            }

            uiEntityContainer.apply {
                when (entityTypeComponent.type) {
                    MapEntityType.START,
                    MapEntityType.FINISH,
                    MapEntityType.CHECKPOINT,
                    MapEntityType.ROCK,
                    MapEntityType.TOWER,
                    MapEntityType.TELEPORT_IN,
                    MapEntityType.TELEPORT_OUT,
                    MapEntityType.SMALL_BLOCKER -> addTo(uiMap.entityLayer)
                    MapEntityType.SPEED_AREA -> addTo(uiMap.speedAreaLayer)
                    MapEntityType.MONSTER -> addTo(uiMap.monsterLayer)
                }
            }



            addComponentOrThrow(UIEntityViewComponent(uiEntityContainer))
            addComponentOrThrow(UIEntityContainerComponent(uiEntityContainer))

            val text = entityTypeComponent.type.getText(entityId, world)
            if (text != null) {
                val textView = makeEntityLabelText(text).apply {
                    addTo(uiMap.entityLabelLayer)
                    scaledHeight = uiMap.gridSize / 2
                    scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
                    centerOn(uiEntityContainer)
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