package com.xenotactic.gamelogic.utils

import korlibs.logger.Logger
import com.xenotactic.gamelogic.model.PlayerData
import korlibs.io.file.std.resourcesVfs
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PlayerDataApi {
    private val mutex = Mutex()
    private val jsonParser = Json {
        prettyPrint = true
    }
    suspend fun getPlayerData(): PlayerData {
        return mutex.withLock lock@{
            logger.info { "Getting player data" }
            val playerDataFile = resourcesVfs["player_data.json"]
            if (playerDataFile.existsBlocking()) {
                return playerDataFile.decodeJson<PlayerData>()!!
            }
            PlayerData()
        }
    }

    suspend fun savePlayerData(playerData: PlayerData) {
        mutex.withLock {
            logger.info { "Saving player data..." }
            val jsonData = jsonParser.encodeToString(playerData)
            resourcesVfs["player_data.json"].writeString(jsonData)
            logger.info { "Finished saving player data." }
        }
    }

    private val logger = Logger<PlayerDataApi>()
}