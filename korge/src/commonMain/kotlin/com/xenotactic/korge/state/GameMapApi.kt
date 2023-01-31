package com.xenotactic.korge.state

import com.soywiz.korma.geom.Rectangle
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.*
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.utils.rectangleIntersects
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.ecomponents.DebugEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.event_listeners.AddedMonsterEntityEvent
import com.xenotactic.korge.event_listeners.AddedUIEntityEvent
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.korge_utils.toRectangleEntity
import com.xenotactic.korge.models.GameWorld
import pathing.PathSequenceTraversal

class GameMapApi(
    val engine: Engine,
) {
    val gameWorld: GameWorld = engine.gameWorld
    val world = gameWorld.world
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

    fun checkNewEntitiesBlocksPath(vararg entities: StagingEntity): Boolean {
        val asRects = entities.map { it.toRectangleEntity() }
        return gameWorld.getPathFindingResult(
            gameMapDimensionsState.width,
            gameMapDimensionsState.height,
            additionalBlockingEntities = asRects
        ) is PathFindingResult.Failure
    }

    fun checkNewEntityIntersectsExistingBlockingEntities(entity: StagingEntity): Boolean {
        val asRect = entity.toRectangleEntity()
        return gameWorld.blockingEntities.any {
            rectangleIntersects(asRect, it.toRectangleEntity())
        }
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
                addFromStagingEntity(entity)
                val entityTypeComponent = entity[com.xenotactic.gamelogic.components.EntityTypeComponent::class]
                when (entityTypeComponent.type) {
                    MapEntityType.START -> Unit
                    MapEntityType.FINISH -> Unit
                    MapEntityType.CHECKPOINT -> Unit
                    MapEntityType.ROCK -> {
                        addComponentOrThrow(SelectableComponent)
                    }

                    MapEntityType.TOWER -> {
                        addComponentOrThrow(BaseDamageComponent(10.0))
                        addComponentOrThrow(MutableDamageUpgradeComponent(0))
                        addComponentOrThrow(DamageMultiplierComponent(1.0))
                        addComponentOrThrow(RangeComponent(7.toGameUnit()))
                        addComponentOrThrow(ReloadTimeComponent(1000.0))
                        addComponentOrThrow(ReloadDowntimeComponent(0.0))
                        addComponentOrThrow(SelectableComponent)
                    }

                    MapEntityType.TELEPORT_IN -> Unit
                    MapEntityType.TELEPORT_OUT -> Unit
                    MapEntityType.SMALL_BLOCKER -> Unit
                    MapEntityType.SPEED_AREA -> Unit
                    MapEntityType.MONSTER -> Unit
                }
//                addOrReplaceComponent(SelectableComponent)
            }
            engine.eventBus.send(AddedUIEntityEvent(entityId))
        }
        updateShortestPath()
    }

    fun spawnCreep() {
        gameWorld.world.addEntity {
            addComponentOrThrow(
                com.xenotactic.gamelogic.components.EntityTypeComponent(
                    MapEntityType.MONSTER
                )
            )
            addComponentOrThrow(com.xenotactic.gamelogic.components.SizeComponent(1.toGameUnit(), 1.toGameUnit()))
            addComponentOrThrow(com.xenotactic.gamelogic.components.MonsterComponent)
            addComponentOrThrow(com.xenotactic.gamelogic.components.VelocityComponent())

            val maxHealthComponent = com.xenotactic.gamelogic.components.MaxHealthComponent(100.0)
            addComponentOrThrow(maxHealthComponent)
            addComponentOrThrow(com.xenotactic.gamelogic.components.HealthComponent(maxHealthComponent.maxHealth))
            addComponentOrThrow(com.xenotactic.gamelogic.components.AnimationComponent(100.0, 0.0))
            addComponentOrThrow(com.xenotactic.gamelogic.components.ComputedSpeedEffectComponent(1.0))


            engine.eventBus.send(AddedMonsterEntityEvent(entityId))

            val pathSequenceTraversal = PathSequenceTraversal(
                gameMapPathState.shortestPath!!
            )
            addComponentOrThrow(
                com.xenotactic.gamelogic.components.PathSequenceTraversalComponent(
                    pathSequenceTraversal
                )
            )
        }
    }

    private fun updateShortestPath() {
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

    fun removeEntities(entities: Set<EntityId>) {
        entities.forEach { gameWorld.world.removeEntity(it) }
        updateShortestPath()
    }

    fun calculateTowerDamage(towerId: EntityId): Double {
        val baseDamageComponent = world[towerId, BaseDamageComponent::class]
        val mutableDamageUpgradeComponent = world[towerId, MutableDamageUpgradeComponent::class]
        val damageMultiplierComponent = world[towerId, DamageMultiplierComponent::class]
        return (baseDamageComponent.damage + mutableDamageUpgradeComponent.numUpgrades) * damageMultiplierComponent.multiplier
    }

}