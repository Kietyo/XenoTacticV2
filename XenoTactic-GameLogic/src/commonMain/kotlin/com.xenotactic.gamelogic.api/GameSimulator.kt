package com.xenotactic.gamelogic.api

import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.model.GameWorld
import com.xenotactic.gamelogic.state.*
import com.xenotactic.gamelogic.system.*
import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.time.Duration.Companion.milliseconds

class GameSimulator(
    width: GameUnit,
    height: GameUnit,
    val engine: Engine,
    val gameWorld: GameWorld,
    val ticksPerSecond: Int = 40,
) {
    val world = gameWorld.world
    val gameMapApi: GameMapApi
    val millisPerTick = (1000.0 / 40).milliseconds
    var tickNum: Long = 0
    init {
        engine.apply {
            stateInjections.setSingletonOrThrow(GameMapDimensionsState(engine, width, height))
            stateInjections.setSingletonOrThrow(GameMapPathState(engine))
            stateInjections.setSingletonOrThrow(GameplayState(61, 0.04, 7))
            stateInjections.setSingletonOrThrow(MutableGoldState(100))
            stateInjections.setSingletonOrThrow(MutableSupplyState())
        }

        gameMapApi = GameMapApi(engine)

        engine.apply {
            injections.setSingletonOrThrow(gameMapApi)
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
        tickNum++
    }
}