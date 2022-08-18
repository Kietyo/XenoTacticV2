package com.xenotactic.gamelogic.pathing

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.PathingBlockingEntity
import com.xenotactic.gamelogic.model.TeleportPair
import com.xenotactic.gamelogic.pathing.GamePath

interface SearcherInterface {
    fun getUpdatablePath(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<MapEntity>,
        teleportPairs: List<TeleportPair> = emptyList(),
        blockingEntities: List<PathingBlockingEntity> = emptyList(),
        blockingPoints: BlockingPointContainer.View? = null
    ): GamePath?
}