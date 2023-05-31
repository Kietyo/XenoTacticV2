package com.xenotactic.gamelogic.utils

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.gamelogic.components.EntitySupplyDepotComponent
import com.xenotactic.gamelogic.state.GameplayState

class StateUtils(
    engine: Engine
) {
    val gameplayState = engine.stateInjections.getSingleton<GameplayState>()
    val gameWorld = engine.gameWorld

    val currentMaxSupply
        get() = gameWorld.calculateMaxSupply(gameplayState.initialSupply, gameplayState.supplyPerDepot, gameplayState.maxSupply)

}