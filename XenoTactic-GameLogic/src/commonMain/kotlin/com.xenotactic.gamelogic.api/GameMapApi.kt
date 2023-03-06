package com.xenotactic.gamelogic.api

import com.soywiz.korma.geom.MRectangle
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.events.AddedEntityEvent
import com.xenotactic.gamelogic.events.AddedMonsterEntityEvent
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.events.RemovedTowerEntityEvent
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.pathing.PathSequenceTraversal
import com.xenotactic.gamelogic.state.GameMapDimensionsState
import com.xenotactic.gamelogic.state.GameMapPathState
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.utils.rectangleIntersects
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.gamelogic.utils.toRectangleEntity
import kotlin.math.pow

class GameMapApi(
    val engine: Engine,
) {
    val gameWorld: GameWorld = engine.gameWorld
    val world = gameWorld.world
    val gameMapPathState = engine.stateInjections.getSingleton<GameMapPathState>()
    val eventBus: EventBus = engine.eventBus
    private val gameMapDimensionsState = engine.stateInjections.getSingleton<GameMapDimensionsState>()
    private val gameplayState = engine.stateInjections.getSingleton<GameplayState>()

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
                val entityTypeComponent = entity[EntityTypeComponent::class]
                when (entityTypeComponent.type) {
                    MapEntityType.START -> Unit
                    MapEntityType.FINISH -> Unit
                    MapEntityType.CHECKPOINT -> Unit
                    MapEntityType.ROCK -> {
                        addComponentOrThrow(SelectableComponent)
                    }

                    MapEntityType.TOWER -> {
                        addComponentOrThrow(BaseDamageComponent(10.0))
                        addComponentOrThrow(DamageUpgradeComponent(0))
                        addComponentOrThrow(SpeedUpgradeComponent(0))
                        addComponentOrThrow(DamageMultiplierComponent(1.0))
                        addComponentOrThrow(RangeComponent(7.toGameUnit()))
                        addComponentOrThrow(BaseWeaponSpeedComponent(1000.0))
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
            engine.eventBus.send(AddedEntityEvent(entityId))
        }
        updateShortestPath()
    }

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
        val pathFinderResult = gameWorld.getPathFindingResult(
            gameMapDimensionsState.width,
            gameMapDimensionsState.height,
        )

        println("pathFinderResult: $pathFinderResult")
        gameMapPathState.updatePath(pathFinderResult.toGamePathOrNull()?.toPathSequence())
    }

    fun getIntersectingEntities(rect: MRectangle): Set<EntityId> {
        return gameWorld.selectableEntitiesFamily.getSequence().mapNotNull {
            val comp = gameWorld.uiEntityViewComponentContainer.getComponent(it)
            if (rect.intersects(comp.entityView.globalBounds)) {
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
        entities.forEach {
            if (gameWorld.world.existsComponent<EntityTowerComponent>(it)) {
                eventBus.send(RemovedTowerEntityEvent(it))
            }
            gameWorld.world.removeEntity(it)
        }
        updateShortestPath()
    }

    fun calculateTowerDamage(towerId: EntityId): Double {
        val baseDamageComponent = world[towerId, BaseDamageComponent::class]
        val damageUpgradeComponent = world[towerId, DamageUpgradeComponent::class]
        val damageMultiplierComponent = world[towerId, DamageMultiplierComponent::class]
        return (baseDamageComponent.damage + damageUpgradeComponent.numUpgrades) * damageMultiplierComponent.multiplier
    }

    fun calculateWeaponSpeedMillis(towerId: EntityId): Double {
        val baseWeaponSpeedComponent = world[towerId, BaseWeaponSpeedComponent::class]
        val speedUpgradeComponent = world[towerId, SpeedUpgradeComponent::class]
        val speedIncreasePerUpgrade = gameplayState.speedPercentPerUpgrade
        return baseWeaponSpeedComponent.millis / (1 + speedIncreasePerUpgrade).pow(speedUpgradeComponent.numUpgrades)
    }

    fun calculateTowerAttacksPerSecond(
        towerId: EntityId,
        weaponSpeed: Double = calculateWeaponSpeedMillis(towerId)
    ): Double {
        return 1E3 / weaponSpeed
    }

}