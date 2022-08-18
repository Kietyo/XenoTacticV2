package com.xenotactic.gamelogic.mapid

import com.soywiz.korio.lang.substr
import com.soywiz.krypto.encoding.hex
import com.soywiz.krypto.sha1
import com.xenotactic.gamelogic.model.GameMap
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
        val asBytes = ProtoBuf.encodeToByteArray(gameMap.toGameMapForId())

        val digest = asBytes.sha1()

        println(digest.hex)

        return digest.hex
    }
}