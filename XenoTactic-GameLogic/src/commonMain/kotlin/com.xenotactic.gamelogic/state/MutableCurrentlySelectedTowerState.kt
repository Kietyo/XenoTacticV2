package com.xenotactic.gamelogic.state

import com.xenotactic.ecs.EntityId
import com.xenotactic.gamelogic.utils.State

data class MutableCurrentlySelectedTowerState(
    var currentTowerId: EntityId?
): State