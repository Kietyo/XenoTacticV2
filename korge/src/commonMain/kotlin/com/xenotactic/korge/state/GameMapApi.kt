package com.xenotactic.korge.state

import com.soywiz.korma.geom.Rectangle
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.components.*
import com.xenotactic.korge.ecomponents.DebugEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.event_listeners.AddedMonsterEntityEvent
import com.xenotactic.korge.event_listeners.AddedUIEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.models.GameWorld
import pathing.PathSequenceTraversal

class GameMapApi(
    val engine: Engine,
) {
    val gameWorld: GameWorld = engine.gameWorld
    val gameMapPathState = engine.injections.getSingleton<GameMapPathState>()
    val eventBus: EventBus = engine.eventBus
    private val gameMapDimensionsState = engine.injections.getSingleton<GameMapDimensionsState>()

    val numCheckpoints
        get() = gameWorld.checkpoints.size
    val numCompletedTeleports
        get() = run {
            val numTpIn = gameWorld.teleportIns.size
            val numTpOut = gameWorld.teleportOuts.size
            minOf(numTpIn, numTpOut)
        }

    fun placeEntitiesV2(otherGameWorld: GameWorld) {
        val entities = otherGameWorld.world.getStagingEntities()
        placeEntitiesV2(entities)
    }

    fun placeEntitiesV2(vararg entities: StagingEntity) {
        placeEntitiesV2(entities.asIterable())
    }

    private fun placeEntitiesV2(entities: Iterable<StagingEntity>) {
        for (entity in entities) {
            val entityId = gameWorld.world.addEntity {
                entity.allComponents.forEach {
                    addComponentOrThrow(it)
                }
                val entityTypeComponent = entity[EntityTypeComponent::class]
                when (entityTypeComponent.type) {
                    MapEntityType.START -> Unit
                    MapEntityType.FINISH -> Unit
                    MapEntityType.CHECKPOINT -> Unit
                    MapEntityType.ROCK -> {
                        addComponentOrThrow(IsSelectableComponent)
                    }

                    MapEntityType.TOWER -> {
                        addComponentOrThrow(RangeComponent(7.toGameUnit()))
                        addComponentOrThrow(ReloadTimeComponent(1000.0))
                        addComponentOrThrow(ReloadDowntimeComponent(0.0))
                    }

                    MapEntityType.TELEPORT_IN -> Unit
                    MapEntityType.TELEPORT_OUT -> Unit
                    MapEntityType.SMALL_BLOCKER -> Unit
                    MapEntityType.SPEED_AREA -> Unit
                    MapEntityType.MONSTER -> Unit
                }
            }
            engine.eventBus.send(AddedUIEntityEvent(entityId))
        }
        updateShortestPath()
    }

//    fun placeEntities(vararg entities: MapEntity) {
//        return placeEntities(entities.asIterable())
//    }

//    private fun placeEntities(entities: Iterable<MapEntity>) {
//        val gameMapRect =
//            GRectInt(0.toGameUnit(), 0.toGameUnit(), gameMapDimensionsState.width, gameMapDimensionsState.height)
//        val allEntitiesIntersectMap = entities.all {
//            rectangleIntersects(gameMapRect, it.getGRectInt())
//        }
//        if (!allEntitiesIntersectMap) return
//
//        for (entity in entities) {
//            val placementEntity = when (entity) {
//                is MapEntity.Rock -> {
//                    val newX = max(entity.x, 0)
//                    val newY = max(entity.y, 0)
//                    val entityWidth = entity.width - (newX - entity.x)
//                    val entityHeight = entity.height - (newY - entity.y)
//                    val newWidth = min(gameMapDimensionsState.width, entity.x + entityWidth) - entity.x
//                    val newHeight = min(gameMapDimensionsState.height, entity.y + entityHeight) - entity.y
//                    MapEntity.Rock(newX, newY, newWidth, newHeight)
//                }
//
//                else -> entity
//            }
//            gameWorld.world.addEntity {

//
//                addComponentOrThrow(mapEntityComponent)
//
//
//                engine.eventBus.send(AddedUIEntityEvent(entityId))
//            }
//
////            gameMap.placeEntity(placementEntity)
//            eventBus.send(AddEntityEvent(placementEntity))
//        }
//        updateShortestPath()
//    }

    fun spawnCreep() {
        gameWorld.world.addEntity {
            addComponentOrThrow(
                EntityTypeComponent(
                    MapEntityType.MONSTER
                )
            )
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
//        var start: IRectangleEntity? = null
//        var finish: IRectangleEntity? = null
//
//        val sequenceNumToPathingEntity = mutableMapOf<Int, IRectangleEntity>()
//        val sequenceNumToTpIn = mutableMapOf<Int, IRectangleEntity>()
//        val sequenceNumToTpOut = mutableMapOf<Int, IRectangleEntity>()
//        val blockingEntities = mutableListOf<IRectangleEntity>()
//
//        gameWorld.entityFamily.getSequence().forEach {
//            val mapEntityComponent = gameWorld.entityTypeComponents.getComponent(it)
//            val sizeComponent = gameWorld.sizeComponent.getComponent(it)
//            val bottomLeftPositionComponent = gameWorld.bottomLeftPositionComponent.getComponent(it)
//            val rectangleEntity = RectangleEntity(
//                bottomLeftPositionComponent.x,
//                bottomLeftPositionComponent.y,
//                sizeComponent.width,
//                sizeComponent.height
//            )
//            when (mapEntityComponent.type) {
//                MapEntityType.START -> start = rectangleEntity
//                MapEntityType.FINISH -> finish = rectangleEntity
//                MapEntityType.CHECKPOINT -> {
//                    val data = gameWorld.world[it, EntityCheckpointComponent::class]
//                    sequenceNumToPathingEntity[data.sequenceNumber] = rectangleEntity
//                }
//
//                MapEntityType.ROCK -> blockingEntities.add(rectangleEntity)
//                MapEntityType.TOWER -> blockingEntities.add(rectangleEntity)
//                MapEntityType.TELEPORT_IN -> {
//                    val data = gameWorld.world[it, EntityTeleportInComponent::class]
//                    sequenceNumToTpIn[data.sequenceNumber] = rectangleEntity
//                }
//
//                MapEntityType.TELEPORT_OUT -> {
//                    val data = gameWorld.world[it, EntityTeleportOutComponent::class]
//                    sequenceNumToTpOut[data.sequenceNumber] = rectangleEntity
//                }
//
//                MapEntityType.SMALL_BLOCKER -> TODO()
//                MapEntityType.SPEED_AREA -> Unit
//                MapEntityType.MONSTER -> TODO()
//            }
//        }
//
//        require(sequenceNumToTpIn.size == sequenceNumToTpOut.size)

//        val pathFinderResult = PathFinder.getUpdatablePath(
//            gameMapDimensionsState.width, gameMapDimensionsState.height,
//            start, finish,
//            blockingEntities = blockingEntities,
//            pathingEntities = sequenceNumToPathingEntity.toList().sortedBy {
//                it.first
//            }.map { it.second },
//            teleportPairs = sequenceNumToTpIn.toList().map {
//                TeleportPair(it.second, sequenceNumToTpOut[it.first]!!, it.first)
//            }.sortedBy {
//                it.sequenceNumber
//            }
//        )

        val pathFinderResult = gameWorld.getPathFindingResult(
            gameMapDimensionsState.width,
            gameMapDimensionsState.height,
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