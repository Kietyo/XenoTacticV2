package com.xenotactic.korge.state

import com.soywiz.korma.geom.Rectangle
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.MapEntityComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.components.UIMapEntityComponent
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.rectangleIntersects
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.ui.UIMapV2
import pathing.PathFinder
import kotlin.math.max
import kotlin.math.min

class GameMapState(
    val eventBus: EventBus,
    val uiMapV2: UIMapV2,
    val gameWorld: World
) {
    private val entityFamily = gameWorld.createFamily(
        FamilyConfiguration(
            allOfComponents = setOf(
                MapEntityComponent::class,
                UIMapEntityComponent::class
            )
        )
    )
    private val mapEntity = gameWorld.getComponentContainer<MapEntityComponent>()
    private val uiMapEntity = gameWorld.getComponentContainer<UIMapEntityComponent>()
    val numCheckpoints
        get() = entityFamily.getSequence().count {
            mapEntity.getComponent(it).entityData is MapEntityData.Checkpoint
        }
    val numCompletedTeleports
        get() = run {
            var numTpIn = 0
            var numTpOut = 0
            entityFamily.getSequence().forEach {
                val comp = mapEntity.getComponent(it)
                if (comp.entityData is MapEntityData.TeleportIn) {
                    numTpIn++
                }
                if (comp.entityData is MapEntityData.TeleportOut) {
                    numTpOut++
                }
            }
            minOf(numTpIn, numTpOut)
        }

    fun placeEntities(vararg entities: MapEntity) {
        val gameMapRect = GRectInt(0, 0, uiMapV2.mapWidth, uiMapV2.mapHeight)
        val allEntitiesIntersectMap = entities.all {
            rectangleIntersects(gameMapRect, it.getGRectInt())
        }
        if (!allEntitiesIntersectMap) return

        for (entity in entities) {
            val placementEntity = when (entity) {
                is MapEntity.Rock -> {
                    val newX = max(entity.x, 0)
                    val newY = max(entity.y, 0)
                    val entityWidth = entity.width - (newX - entity.x)
                    val entityHeight = entity.height - (newY - entity.y)
                    val newWidth = min(uiMapV2.mapWidth, entity.x + entityWidth) - entity.x
                    val newHeight = min(uiMapV2.mapHeight, entity.y + entityHeight) - entity.y
                    MapEntity.Rock(newX, newY, newWidth, newHeight)
                }

                else -> entity
            }
            gameWorld.addEntity {
                when (placementEntity) {
                    is MapEntity.Checkpoint -> {
                        addComponentOrThrow(
                            MapEntityComponent(
                                MapEntityData.Checkpoint(
                                    placementEntity.sequenceNumber
                                )
                            )
                        )
                    }

                    is MapEntity.Finish -> {
                        addComponentOrThrow(
                            MapEntityComponent(
                                MapEntityData.Finish
                            )
                        )
                    }
                    is MapEntity.Rock -> {
                        addComponentOrThrow(
                            MapEntityComponent(
                                MapEntityData.Rock
                            )
                        )
                    }
                    is MapEntity.SmallBlocker -> TODO()
                    is MapEntity.SpeedArea -> TODO()
                    is MapEntity.Start -> {
                        addComponentOrThrow(
                            MapEntityComponent(
                                MapEntityData.Start
                            )
                        )
                    }
                    is MapEntity.TeleportIn -> {
                        addComponentOrThrow(
                            MapEntityComponent(
                                MapEntityData.TeleportIn(placementEntity.sequenceNumber)
                            )
                        )
                    }
                    is MapEntity.TeleportOut -> {
                        addComponentOrThrow(
                            MapEntityComponent(
                                MapEntityData.TeleportOut(placementEntity.sequenceNumber)
                            )
                        )
                    }
                    is MapEntity.Tower -> TODO()
                }

                addComponentOrThrow(SizeComponent(placementEntity.width, placementEntity.height))
                addComponentOrThrow(BottomLeftPositionComponent(placementEntity.x, placementEntity.y))
            }

//            gameMap.placeEntity(placementEntity)
            eventBus.send(AddEntityEvent(placementEntity))
        }
        TODO()
//        updateShortestPath(PathFinder.getShortestPath(gameMap))
    }

    fun getIntersectingEntities(rect: Rectangle): List<UIEntity> {
        return entityFamily.getSequence().mapNotNull {
            val comp = uiMapEntity.getComponent(it)
            if (rect.intersects(comp.entityView.getGlobalBounds())) {
                comp.entityView
            } else {
                null
            }
        }.toList()
    }

    fun getNotificationText(entityType: MapEntityType): String {
        val entityName = when (entityType) {
            MapEntityType.START -> "Start"
            MapEntityType.FINISH -> "Finish"
            MapEntityType.CHECKPOINT -> {
                "Checkpoint ${numCheckpoints + 1}"
            }

            MapEntityType.ROCK -> "Rock"
            MapEntityType.TOWER -> TODO()
            MapEntityType.TELEPORT_IN -> "Teleport In ${numCompletedTeleports + 1}"
            MapEntityType.TELEPORT_OUT -> "Teleport Out ${numCompletedTeleports + 1}"
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
        }

        return "Placement Mode: $entityName"
    }
}