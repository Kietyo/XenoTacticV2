package com.xenotactic.gamelogic.model

import kotlinx.serialization.Serializable

@Serializable
data class FbGameMap(
    val width: Int,
    val height: Int,
    val start: MapEntity.Start? = null,
    val finish: MapEntity.Finish? = null,
    val checkpoints: List<MapEntity.Checkpoint> = emptyList(),
    val teleportIns: List<MapEntity.TeleportIn> = emptyList(),
    val teleportOuts: List<MapEntity.TeleportOut> = emptyList(),
    val towers: List<MapEntity.Tower> = emptyList(),
    val rocks: List<MapEntity.Rock> = emptyList(),
    val smallBlockers: List<MapEntity.SmallBlocker> = emptyList(),
    val speedAreas: List<MapEntity.SpeedArea> = emptyList(),
)

@Serializable
data class FbMapData(
    val data: Map<String, FbMapEntry>
)

@Serializable
data class FbMapEntry(
    val data: FbGameMap,
    val timestamp: Long
)