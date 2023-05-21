package com.xenotactic.gamelogic.model

import com.xenotactic.gamelogic.utils.GameUnit

sealed class MapEntityData {
    object Start : MapEntityData()
    object Finish : MapEntityData()
    object Rock : MapEntityData()
    object Tower : MapEntityData()
    object SupplyDepot : MapEntityData()
    object SmallBlocker : MapEntityData()
    data class TeleportIn(val sequenceNumber: Int) : MapEntityData() {
        val ordinalSequenceNumber get() = sequenceNumber + 1
    }
    data class TeleportOut(val sequenceNumber: Int) : MapEntityData() {
        val ordinalSequenceNumber get() = sequenceNumber + 1
    }
    data class Checkpoint(val sequenceNumber: Int) : MapEntityData() {
        val ordinalSequenceNumber get() = sequenceNumber + 1
    }
    data class SpeedArea(val radius: GameUnit, val speedEffect: Double) : MapEntityData() {
        val speedText get() = "${(speedEffect * 100).toInt()}%"
    }

    object Monster : MapEntityData()

    fun toMapEntityType(): MapEntityType {
        return when (this) {
            is Checkpoint -> MapEntityType.CHECKPOINT
            Finish -> MapEntityType.FINISH
            Rock -> MapEntityType.ROCK
            SmallBlocker -> MapEntityType.SMALL_BLOCKER
            is SpeedArea -> MapEntityType.SPEED_AREA
            Start -> MapEntityType.START
            is TeleportIn -> MapEntityType.TELEPORT_IN
            is TeleportOut -> MapEntityType.TELEPORT_OUT
            Tower -> MapEntityType.TOWER
            Monster -> MapEntityType.MONSTER
            SupplyDepot -> MapEntityType.SUPPLY_DEPOT
        }
    }

    fun getText(): String? {
        return when(this) {
            is Checkpoint -> "CP ${ordinalSequenceNumber}"
            Finish -> "FINISH"
            Rock -> null
            SmallBlocker -> null
            is SpeedArea -> "${speedText}"
            Start -> "START"
            is TeleportIn -> "TP ${ordinalSequenceNumber} IN"
            is TeleportOut -> "TP ${ordinalSequenceNumber} OUT"
            Tower -> null
            Monster -> null
            SupplyDepot -> null
        }
    }
}