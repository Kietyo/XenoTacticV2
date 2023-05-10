package com.xenotactic.gamelogic.utils

import com.xenotactic.gamelogic.model.GameMap
import korlibs.crypto.sha1
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

object MapToId {
    /**
     * Calculates an ID for the provided game map.
     *
     * It does so by converting the game map into a protocol buffer
     * byte buffer, then hashing the result using SHA-1.
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun calculateId(gameMap: GameMap): String {
        TODO()
        val asBytes = ProtoBuf.encodeToByteArray(gameMap.toGameMapForId())

        val digest = asBytes.sha1()

        println(digest.hex)

        return digest.hex
    }
}