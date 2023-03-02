package com.xenotactic.gamelogic.api

import com.soywiz.klock.TimeSpan
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.state.*
import com.xenotactic.gamelogic.system.MonsterComputeSpeedEffectSystem
import com.xenotactic.gamelogic.system.MonsterMoveSystem
import com.xenotactic.gamelogic.system.MonsterRemoveSystem
import com.xenotactic.gamelogic.utils.GameUnit
import kotlin.time.Duration.Companion.milliseconds

class GameSimulator(
    width: GameUnit,
    height: GameUnit,
    val engine: Engine,
    val world: World,
) {
    init {
        engine.apply {
            stateInjections.setSingletonOrThrow(GameMapDimensionsState(engine, width, height))
            stateInjections.setSingletonOrThrow(GameMapPathState(engine))
            stateInjections.setSingletonOrThrow(GameplayState(61, 0.04, 7))
            stateInjections.setSingletonOrThrow(MutableGoldState(100))
            stateInjections.setSingletonOrThrow(MutableSupplyState())
        }
        world.apply {
            addSystem(MonsterMoveSystem(this))
            addSystem(MonsterRemoveSystem(this))
            addSystem(MonsterComputeSpeedEffectSystem(engine))
        }
    }

    fun update(deltaTime: TimeSpan) {
        world.update(deltaTime.milliseconds.milliseconds)
    }
}