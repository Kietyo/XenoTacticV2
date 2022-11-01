package com.xenotactic.korge.state

import com.soywiz.korma.geom.Rectangle
import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.utils.max
import com.xenotactic.gamelogic.utils.min
import com.xenotactic.gamelogic.utils.rectangleIntersects
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.ecomponents.DebugEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.event_listeners.AddedMonsterEntityEvent
import com.xenotactic.korge.event_listeners.AddedUIEntityEvent
import com.xenotactic.korge.events.AddEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.models.GameWorld
import pathing.PathFinder
import pathing.PathSequenceTraversal

class GameMapApi(
    val engine: Engine,
) {
    val gameWorld: GameWorld = engine.gameWorld
    val gameMapPathState = engine.injections.getSingleton<GameMapPathState>()
    val eventBus: EventBus = engine.eventBus
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


    fun placeEntities(vararg entities: MapEntity) {
        return placeEntities(entities.asIterable())
    }

    fun placeEntities(entities: Iterable<MapEntity>) {
        val gameMapRect =
            GRectInt(0.toGameUnit(), 0.toGameUnit(), gameMapDimensionsState.width, gameMapDimensionsState.height)
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
                val mapEntityComponent: MapEntityComponent = when (placementEntity) {
                    is MapEntity.Checkpoint -> {
                        MapEntityComponent(
                            MapEntityData.Checkpoint(
                                placementEntity.sequenceNumber
                            )
                        )
                    }

                    is MapEntity.Finish -> {
                        MapEntityComponent(
                            MapEntityData.Finish
                        )
                    }

                    is MapEntity.Rock -> {
                        addComponentOrThrow(IsSelectableComponent)
                        MapEntityComponent(
                            MapEntityData.Rock
                        )
                    }

                    is MapEntity.SmallBlocker -> TODO()
                    is MapEntity.SpeedArea -> {
                        val data = MapEntityData.SpeedArea(placementEntity.radius, placementEntity.speedEffect)
                        addComponentOrThrow(SpeedAreaEffectComponent(data))
                        MapEntityComponent(
                            data
                        )
                    }

                    is MapEntity.Start -> {
                        MapEntityComponent(
                            MapEntityData.Start
                        )
                    }

                    is MapEntity.TeleportIn -> {
                        MapEntityComponent(
                            MapEntityData.TeleportIn(placementEntity.sequenceNumber)
                        )
                    }

                    is MapEntity.TeleportOut -> {
                        MapEntityComponent(
                            MapEntityData.TeleportOut(placementEntity.sequenceNumber)
                        )
                    }

                    is MapEntity.Tower -> {
                        addComponentOrThrow(TowerComponent)
                        addComponentOrThrow(RangeComponent(7.toGameUnit()))
                        addComponentOrThrow(ReloadTimeComponent(1000.0))
                        addComponentOrThrow(ReloadDowntimeComponent(0.0))
                        MapEntityComponent(
                            MapEntityData.Tower
                        )
                    }
                }

                addComponentOrThrow(mapEntityComponent)
                addComponentOrThrow(SizeComponent(placementEntity.width, placementEntity.height))

                addComponentOrThrow(
                    BottomLeftPositionComponent(
                        placementEntity.x,
                        placementEntity.y
                    )
                )

                engine.eventBus.send(AddedUIEntityEvent(entityId))
            }

//            gameMap.placeEntity(placementEntity)
            eventBus.send(AddEntityEvent(placementEntity))
        }
        updateShortestPath()
    }

    fun spawnCreep() {
        gameWorld.world.addEntity {
            addComponentOrThrow(MapEntityComponent(
                MapEntityData.Monster
            ))
            addComponentOrThrow(SizeComponent(1.toGameUnit(), 1.toGameUnit()))
            addComponentOrThrow(MonsterComponent)
            addComponentOrThrow(VelocityComponent())

            val maxHealthComponent = MaxHealthComponent(100.0)
            addComponentOrThrow(maxHealthComponent)
            addComponentOrThrow(HealthComponent(maxHealthComponent.maxHealth))
            addComponentOrThrow(AnimationComponent(100.0, 0.0))
            addComponentOrThrow(ComputedSpeedEffectComponent(1.0))

            engine.eventBus.send(AddedMonsterEntityEvent(entityId))

            val pathSequenceTraversal = PathSequenceTraversal(
                gameMapPathState.shortestPath!!
            )
            addComponentOrThrow(
                PathSequenceTraversalComponent(
                    pathSequenceTraversal
                )
            )
        }
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
                is MapEntityData.SpeedArea -> Unit
                is MapEntityData.TeleportIn -> {
                    sequenceNumToTpIn[entityData.sequenceNumber] = rectangleEntity
                }

                is MapEntityData.TeleportOut -> {
                    sequenceNumToTpOut[entityData.sequenceNumber] = rectangleEntity
                }

                MapEntityData.Tower -> blockingEntities.add(rectangleEntity)
                MapEntityData.Monster -> TODO()
            }
        }

        require(sequenceNumToTpIn.size == sequenceNumToTpOut.size)

        val pathFinderResult = PathFinder.getUpdatablePath(
            gameMapDimensionsState.width, gameMapDimensionsState.height,
            start, finish,
            blockingEntities = blockingEntities,
            pathingEntities = sequenceNumToPathingEntity.toList().sortedBy {
                it.first
            }.map { it.second },
            teleportPairs = sequenceNumToTpIn.toList().map {
                TeleportPair(it.second, sequenceNumToTpOut[it.first]!!, it.first)
            }.sortedBy {
                it.sequenceNumber
            }
        )

        println("pathFinderResult: $pathFinderResult")
        gameMapPathState.updatePath(pathFinderResult.toGamePathOrNull()?.toPathSequence())

        engine.injections.getSingletonOrNull<DebugEComponent>()?.updatePathingPoints()
    }

    fun getIntersectingEntities(rect: Rectangle): Set<EntityId> {
        return gameWorld.selectableEntitiesFamily.getSequence().mapNotNull {
            val comp = gameWorld.uiEntityViewComponentContainer.getComponent(it)
            if (rect.intersects(comp.entityView.getGlobalBounds())) {
                it
            } else {
                null
            }
        }.toSet()
    }

    fun getNotificationText(entityType: MapEntityType): String {
        val entityName = when (entityType) {
            MapEntityType.START -> "Start"
            MapEntityType.FINISH -> "Finish"
            MapEntityType.CHECKPOINT -> {
                "Checkpoint ${numCheckpoints + 1}"
            }

            MapEntityType.ROCK -> "Rock"
            MapEntityType.TOWER -> "Tower"
            MapEntityType.TELEPORT_IN -> "Teleport In ${numCompletedTeleports + 1}"
            MapEntityType.TELEPORT_OUT -> "Teleport Out ${numCompletedTeleports + 1}"
            MapEntityType.SMALL_BLOCKER -> TODO()
            MapEntityType.SPEED_AREA -> TODO()
            MapEntityType.MONSTER -> TODO()
        }

        return "Placement Mode: $entityName"
    }
}