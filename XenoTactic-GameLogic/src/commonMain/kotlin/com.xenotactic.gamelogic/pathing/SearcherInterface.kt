package com.xenotactic.gamelogic.pathing

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair

interface SearcherInterface {
    fun getUpdatablePath(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<RectangleEntity>,
        teleportPairs: List<TeleportPair> = emptyList(),
        blockingEntities: List<RectangleEntity> = emptyList(),
        blockingPoints: BlockingPointContainer.View? = null
    ): GamePath?
}