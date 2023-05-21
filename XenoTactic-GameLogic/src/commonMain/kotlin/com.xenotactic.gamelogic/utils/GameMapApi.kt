package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.events.AddedMonsterEntityEvent
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.pathing.PathFindingResult
import com.xenotactic.gamelogic.pathing.PathSequenceTraversal
import com.xenotactic.gamelogic.state.GameMapDimensionsState
import com.xenotactic.gamelogic.state.GameMapPathState
import com.xenotactic.gamelogic.state.GameplayState
import com.xenotactic.gamelogic.state.MutableEventQueueState
import korlibs.math.geom.Rectangle
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
    private val mutableEventQueueState = engine.stateInjections.getSingleton<MutableEventQueueState>()

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

    fun placeEntities(otherGameWorld: GameWorld) {
        val entities = otherGameWorld.world.getStagingEntities()
        placeEntities(entities)
    }

    fun placeEntities(vararg entities: StagingEntity) = placeEntities(entities.asIterable())

    private fun placeEntities(entities: Iterable<StagingEntity>) {
        mutableEventQueueState.add(GameEvent.PlaceEntities(entities.toList()))
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



    fun getIntersectingEntities(rect: Rectangle): Set<EntityId> {
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
            MapEntityType.SUPPLY_DEPOT -> "Supply depot"
        }

        return "Placement Mode: $entityName"
    }

    fun removeEntities(entities: Set<EntityId>) {
        mutableEventQueueState.add(GameEvent.RemoveEntities(entities))
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

    fun isAtMaxSupply(): Boolean {
        val currentSupplyLimit = gameplayState.initialMaxSupply
        val towers = gameWorld.towers
        require(towers.size <= currentSupplyLimit)
        return towers.size == currentSupplyLimit
    }

}