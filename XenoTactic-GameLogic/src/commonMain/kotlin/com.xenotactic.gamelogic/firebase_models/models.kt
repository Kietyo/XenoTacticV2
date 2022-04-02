package com.xenotactic.gamelogic.firebase_models

import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class FbGameMap(
    @SerialName("000_width")
    val width: Int,
    @SerialName("001_height")
    val height: Int,
    @SerialName("002_start")
    val start: MapEntity.Start? = null,
    @SerialName("003_finish")
    val finish: MapEntity.Finish? = null,
    @SerialName("004_checkpoints")
    val checkpoints: List<MapEntity.CheckPoint> = emptyList(),
    @SerialName("005_teleportIns")
    val teleportIns: List<MapEntity.TeleportIn> = emptyList(),
    @SerialName("006_teleportOuts")
    val teleportOuts: List<MapEntity.TeleportOut> = emptyList(),
    @SerialName("007_towers")
    val towers: List<MapEntity.Tower> = emptyList(),
    @SerialName("008_rocks")
    val rocks: List<MapEntity.Rock>,
    @SerialName("009_smallBlockers")
    val smallBlockers: List<MapEntity.SmallBlocker> = emptyList(),
    @SerialName("010_speedAreas")
    val speedAreas: List<MapEntity.SpeedArea> = emptyList(),
) {
//    fun toGameMap() {
//        return GameMap(
//            width,
//            height,
//            start,
//            finish,
//            checkpoints.toMutableList(),
//            tele
//        )
//    }
}

@Serializable
data class FbMapData(
    val data: Map<String, FbMapEntry>
)

@Serializable
data class FbMapEntry(
    val data: FbGameMap,
    val timestamp: Long
)