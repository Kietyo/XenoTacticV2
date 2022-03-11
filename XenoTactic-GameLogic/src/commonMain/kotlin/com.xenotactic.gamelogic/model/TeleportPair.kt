package model

import com.xenotactic.gamelogic.model.MapEntity

data class TeleportPair(
    val teleportIn: MapEntity.TeleportIn,
    val teleportOut: MapEntity.TeleportOut
) {
    init {
        require(teleportIn.sequenceNumber == teleportOut.sequenceNumber)
    }
    val sequenceNumber = teleportIn.sequenceNumber
}
