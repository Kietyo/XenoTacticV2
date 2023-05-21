package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.events.AddedEntityEvent
import com.xenotactic.gamelogic.events.RemovedTowerEntityEvent
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.state.*
import com.xenotactic.gamelogic.system.*
import kotlin.time.Duration.Companion.milliseconds

sealed class GameEvent {
    data class PlaceEntities(
        val entities: Collection<StagingEntity>
    ): GameEvent()
    data class RemoveEntities(
        val entities: Set<EntityId>
    ): GameEvent()
}

class GameSimulator(
    width: GameUnit,
    height: GameUnit,
    val engine: Engine,
    val ticksPerSecond: Int = 60,
) {
    val gameWorld = engine.gameWorld
    val eventBus = engine.eventBus
    val world = gameWorld.world
    val gameMapApi: GameMapApi
    val millisPerTick = (1000.0 / ticksPerSecond).milliseconds
    var tickNum: Long = 0
        private set

    private val eventLog = EventLog()

    private val gameMapDimensionsState = GameMapDimensionsState(engine, width, height)
    private val gameMapPathState = GameMapPathState(engine)
    private val gameplayState = GameplayState(61, 0.04, 7, 4, 5, 31)
    private val mutableEventQueueState = MutableEventQueueState()
    private val mutableGoldState = MutableGoldState(200, eventBus)
    private val stateUtils: StateUtils

    init {
        engine.apply {
            stateInjections.setSingletonOrThrow(gameMapDimensionsState)
            stateInjections.setSingletonOrThrow(gameMapPathState)
            stateInjections.setSingletonOrThrow(mutableEventQueueState)
            stateInjections.setSingletonOrThrow(gameplayState)
            stateInjections.setSingletonOrThrow(mutableGoldState)
            injections.setSingletonOrThrow(this@GameSimulator)
        }

        gameMapApi = GameMapApi(engine)
        stateUtils = StateUtils(engine)

        engine.apply {
            injections.setSingletonOrThrow(gameMapApi)
            injections.setSingletonOrThrow(stateUtils)
        }

        world.apply {
            addSystem(MonsterMoveSystem(this))
            addSystem(MonsterRemoveSystem(this))
            addSystem(MonsterComputeSpeedEffectSystem(engine))

            addSystem(ProjectileRemoveSystem(this))
            addSystem(TowerTargetingRemoveSystem(this))
            addSystem(TargetingAddSystem(gameWorld))

            addSystem(ProjectileMoveSystem(this))
            addSystem(ProjectileCollideSystem(this))

            addSystem(MonsterDeathSystem(engine))
            addSystem(ReloadSystem(engine))
            addSystem(TowerAttackSystem(this, gameMapApi))
        }
    }

    fun tick() {
        world.update(millisPerTick)

        if (mutableEventQueueState.isNotEmpty) {
            val eventsToProcess = mutableEventQueueState.toList()

            for (event in eventsToProcess) {
                when (event) {
                    is GameEvent.PlaceEntities -> handlePlaceEntitiesEvent(event)
                    is GameEvent.RemoveEntities -> handleRemoveEntitiesEvent(event)
                }
            }

            eventLog.recordEntry(LogEntry(tickNum, eventsToProcess))

            mutableEventQueueState.clearEvents()
        }

        tickNum++
    }

    fun printEventLog() {
        println(eventLog)
    }

    private fun handleRemoveEntitiesEvent(event: GameEvent.RemoveEntities) {
        event.entities.forEach {
            val isTowerEntity = gameWorld.world.existsComponent<EntityTowerComponent>(it)

            if (isTowerEntity) {
                mutableGoldState.currentGold += gameplayState.basicTowerCost
            }

            gameWorld.world.modifyEntity(it) {
                removeThisEntity()
            }
            if (isTowerEntity) {
                eventBus.send(RemovedTowerEntityEvent(it))
            }
        }
        updateShortestPath()
    }

    private fun handlePlaceEntitiesEvent(event: GameEvent.PlaceEntities) {
        for (entity in event.entities) {
            val entityTypeComponent = entity[EntityTypeComponent::class]

            if (entityTypeComponent.type == MapEntityType.TOWER) {
                // check we have enough gold
                val towerCost = gameplayState.basicTowerCost
                if (towerCost > mutableGoldState.currentGold) {
                    break
                }

                val towerSupplyCost = entity[SupplyCostComponent::class]
                val currentSupplyUsage = gameWorld.currentSupplyUsage
                val currentMaxSupplyCost = stateUtils.currentMaxSupply
                if ((towerSupplyCost.cost + currentSupplyUsage) > currentMaxSupplyCost) {
                    break
                }

                mutableGoldState.currentGold -= towerCost
            }

            val entityId = gameWorld.world.addEntity {
                addComponentsFromStagingEntity(entity)
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
                    MapEntityType.SUPPLY_DEPOT -> {
                        addComponentOrThrow(SelectableComponent)
                    }
                }
            }

            eventBus.send(AddedEntityEvent(entityId))
        }
        updateShortestPath()
    }

    private fun updateShortestPath() {
        val pathFinderResult = gameWorld.getPathFindingResult(
            gameMapDimensionsState.width,
            gameMapDimensionsState.height,
        )

        gameMapPathState.updatePath(pathFinderResult.toGamePathOrNull()?.toPathSequence())
    }
}