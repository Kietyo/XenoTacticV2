package com.xenotactic.gamelogic.utils

import com.xenotactic.gamelogic.state.GameplayState

class StateUtils(
    engine: Engine
) {
    val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    val gameWorld = engine.gameWorld

    val currentMaxSupply
        get() =
            gameWorld.supplyDepotsFamily.size * gameplayState.supplyPerDepot + gameplayState.initialMaxSupply

}