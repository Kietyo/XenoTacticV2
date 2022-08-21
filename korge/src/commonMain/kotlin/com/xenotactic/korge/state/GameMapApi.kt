package com.xenotactic.korge.state

import com.soywiz.korma.geom.Rectangle
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.components.BottomLeftPositionComponent
import com.xenotactic.gamelogic.components.MapEntityComponent
import com.xenotactic.gamelogic.components.SizeComponent
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.rectangleIntersects
import com.xenotactic.korge.ecomponents.DebugEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.UpdatedPathLineEvent
import com.xenotactic.korge.models.GameWorld
import pathing.PathFinder
import kotlin.math.max
import kotlin.math.min

class GameMapApi(
    val engine: Engine,
    val eventBus: EventBus,
) {
    val gameWorld: GameWorld = engine.gameWorld
    private val gameMapDimensionsState = engine.injections.getSingleton<GameMapDimensionsState>()

    val numCheckpoints
        get() = gameWorld.entityFamily.getSequence().count {
            gameWorld.mapEntityComponent.getComponent(it).entityData is MapEntityData.Checkpoint
        }
    val numCompletedTeleports
        get() = run {
            var numTpIn = 0
            var numTpOut = 0
            gameWorld.entityFamily.getSequence().forEach {
                val comp = gameWorld.mapEntityComponent.getComponent(it)
                if (comp.entityData is MapEntityData.TeleportIn) {
                    numTpIn++
                }
                if (comp.entityData is MapEntityData.TeleportOut) {
                    numTpOut++
                }
            }
            minOf(numTpIn, numTpOut)
        }

    var shortestPath: PathSequence? = null
        private set

    fun placeEntities(vararg entities: MapEntity) {
        val gameMapRect = GRectInt(0, 0, gameMapDimensionsState.width, gameMapDimensionsState.height)
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
                    val newWidth = min(gameMapDimensionsState.width, entity.x + entityWidth) - entity.x
                    val newHeight = min(gameMapDimensionsState.height, entity.y + entityHeight) - entity.y
                    MapEntity.Rock(newX, newY, newWidth, newHeight)
                }
                else -> entity
            }
            gameWorld.world.addEntity {
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
                addComponentOrThrow(
                    BottomLeftPositionComponent(
                        placementEntity.x,
                        placementEntity.y
                    )
                )
            }

//            gameMap.placeEntity(placementEntity)
            eventBus.send(AddEntityEvent(placementEntity))
        }
        updateShortestPath()
    }

    private fun updateShortestPath() {
        var start: IRectangleEntity? = null
        var finish: IRectangleEntity? = null

        val sequenceNumToPathingEntity = mutableMapOf<Int, IRectangleEntity>()
        val sequenceNumToTpIn = mutableMapOf<Int, IRectangleEntity>()
        val sequenceNumToTpOut = mutableMapOf<Int, IRectangleEntity>()
        val blockingEntities = mutableListOf<IRectangleEntity>()

        gameWorld.entityFamily.getSequence().forEach {
            val mapEntityComponent = gameWorld.mapEntityComponent.getComponent(it)
            val sizeComponent = gameWorld.sizeComponent.getComponent(it)
            val bottomLeftPositionComponent = gameWorld.bottomLeftPositionComponent.getComponent(it)
            val entityData = mapEntityComponent.entityData
            val rectangleEntity = RectangleEntity(
                bottomLeftPositionComponent.x,
                bottomLeftPositionComponent.y,
                sizeComponent.width,
                sizeComponent.height
            )
            when (entityData) {
                MapEntityData.Start -> {
                    start = rectangleEntity
                }

                MapEntityData.Finish -> {
                    finish = rectangleEntity
                }

                is MapEntityData.Checkpoint -> {
                    sequenceNumToPathingEntity[entityData.sequenceNumber] = rectangleEntity
                }

                MapEntityData.Rock -> {
                    blockingEntities.add(rectangleEntity)
                }
                MapEntityData.SmallBlocker -> TODO()
                is MapEntityData.SpeedArea -> TODO()
                is MapEntityData.TeleportIn -> {
                    sequenceNumToTpIn[entityData.sequenceNumber] = rectangleEntity
                }

                is MapEntityData.TeleportOut -> {
                    sequenceNumToTpOut[entityData.sequenceNumber] = rectangleEntity
                }

                MapEntityData.Tower -> TODO()
            }
        }

        require(sequenceNumToTpIn.size == sequenceNumToTpOut.size)

        shortestPath = PathFinder.getUpdatablePath(
            gameMapDimensionsState.height, gameMapDimensionsState.height,
            start, finish,
            blockingEntities = blockingEntities,
            pathingEntities = sequenceNumToPathingEntity.toList().sortedBy {
                it.first
            }.map { it.second }, teleportPairs = sequenceNumToTpIn.toList().map {
                TeleportPair(it.second, sequenceNumToTpOut[it.first]!!, it.first)
            }.sortedBy {
                it.sequenceNumber
            }
        )?.toPathSequence()

        engine.injections.getSingletonOrNull<DebugEComponent>()?.updatePathingPoints()

        eventBus.send(
            UpdatedPathLineEvent(
                shortestPath,
                shortestPath?.pathLength
            )
        )
    }

    fun getIntersectingEntities(rect: Rectangle): List<EntityId> {
        return gameWorld.entityFamily.getSequence().mapNotNull {
            val comp = gameWorld.uiMapEntityComponentContainer.getComponent(it)
            if (rect.intersects(comp.entityView.getGlobalBounds())) {
                it
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