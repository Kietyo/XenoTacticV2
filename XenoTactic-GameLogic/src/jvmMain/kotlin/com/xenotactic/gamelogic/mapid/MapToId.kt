package com.xenotactic.gamelogic.mapid

import com.soywiz.korio.lang.substr
import com.soywiz.krypto.encoding.hex
import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import verify
import java.security.MessageDigest

object MapToId {

    /**
     * Calculates an ID for the provided game map.
     *
     * It does so by converting the game map into a protocol buffer
     * byte buffer, then hashing the result using SHA-1.
     */
    fun calculateId(gameMap: GameMap): String {
        require(gameMap.verify() == MapVerificationResult.Success)

        val md = MessageDigest.getInstance("SHA-1")

        println("gameMap before: $gameMap")

        val asBytes = ProtoBuf.encodeToByteArray(gameMap)

        val gameMapAfterDecode = ProtoBuf.decodeFromByteArray<GameMap>(asBytes)

        println("gameMap after: $gameMapAfterDecode")

        println("asBytes: ${asBytes.joinToString(" ") { it.hex.substr(2) } }")

        val digest = md.digest(asBytes)

        println(digest.joinToString(" ") { it.hex.substr(2) })

        println(digest.hex)

        return digest.hex
    }
}