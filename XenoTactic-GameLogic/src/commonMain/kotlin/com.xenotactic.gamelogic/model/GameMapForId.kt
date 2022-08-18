package com.xenotactic.gamelogic.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * A copy of the GameMap model specifically for use in generating
 * a unique ID.
 *
 * A copy of the GameMap model was created to decouple the model
 * used for generating the ID from the actual model.
 */
@Serializable
data class GameMapForId @OptIn(ExperimentalSerializationApi::class) constructor(
    @ProtoNumber(1)
    val width: Int,
    @ProtoNumber(2)
    val height: Int,
    @ProtoNumber(3)
    val checkpoints: List<MapEntity.Checkpoint>,
    @ProtoNumber(4)
    val teleportIns: Map<Int, MapEntity.TeleportIn>,
    @ProtoNumber(5)
    val teleportOuts: Map<Int, MapEntity.TeleportOut>,
    @ProtoNumber(6)
    val towers: List<MapEntity.Tower>,
    @ProtoNumber(7)
    val rocks: List<MapEntity.Rock>,
    @ProtoNumber(8)
    val smallBlockers: List<MapEntity.SmallBlocker>,
    @ProtoNumber(9)
    val speedAreas: List<MapEntity.SpeedArea> = emptyList(),
) {
    init {
        require(checkpoints.map { it.sequenceNumber }.toSet().size == checkpoints.size) {
            "The number of unique checkpoints and sequence numbers do not match up!"
        }
        for ((i, cp) in checkpoints.withIndex()) {
            require(i == cp.sequenceNumber) {
                "Checkpoints should be ordered by sequence number!"
            }
        }
    }
}