package com.xenotactic.gamelogic.daos

import com.soywiz.korio.net.http.*
import com.soywiz.korio.stream.openAsync
import com.xenotactic.gamelogic.firebase_models.FbMapData
import com.xenotactic.gamelogic.firebase_models.FbMapEntry
import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.gamelogic.model.GameMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmInline

class DatastoreKorge(val accessToken: String = "") {
    val restClient =
        createHttpClientEndpoint("https://xenotactic-default-rtdb.firebaseio.com").rest()
    val client = createHttpClient {
    }

    init {

    }

    suspend fun mapExists(gameMap: GameMap): Boolean {
        val id = MapToId.calculateId(gameMap)
        return mapExists(id)
    }

    suspend fun mapExists(mapId: String): Boolean {
        return client.request(
            Http.Method.GET,
            "https://xenotactic-default-rtdb.firebaseio.com/maps/$mapId/timestamp.json"
        ).readAllString() != "null"
    }

    suspend fun addMapIfNotExists(map: GameMap) {
        val id = MapToId.calculateId(map)
        if (mapExists(id)) {
            return
        }
        val fbMap = map.toFbGameMap()
        client.request(
            Http.Method.PUT,
            "https://xenotactic-default-rtdb.firebaseio.com/maps/$id/data.json",
            content = Json.encodeToString(fbMap).openAsync()
        )
        client.request(
            Http.Method.PUT,
            "https://xenotactic-default-rtdb.firebaseio.com/maps/$id/timestamp.json",
            content = """
                { ".sv": "timestamp"}
            """.trimIndent().openAsync()
        )
    }

    suspend fun getMapEntry(map: GameMap): FbMapEntry {
        val id = MapToId.calculateId(map)
        val response = client.request(
            Http.Method.GET,
            "https://xenotactic-default-rtdb.firebaseio.com/maps/$id.json",
        )
        println(response)

        val str = response.readAllString()
        println(str)

        return Json.decodeFromString<FbMapEntry>(str)
    }

    suspend fun deleteMap(map: GameMap) {
        val id = MapToId.calculateId(map)
        val response = client.request(
            Http.Method.DELETE,
            "https://xenotactic-default-rtdb.firebaseio.com/maps/$id.json",
        )

        println("delete response: $response")
    }

    suspend fun deleteAllMaps() {
        val response = client.request(
            Http.Method.DELETE,
            "https://xenotactic-default-rtdb.firebaseio.com/maps.json",
        )

        println("delete response: $response")
    }

    suspend fun getAllMapData(): FbMapData {
        val response = client.request(
            Http.Method.GET,
            "https://xenotactic-default-rtdb.firebaseio.com/maps.json",
        )

        val str = "{\"data\":${response.readAllString()}}"

        println("str:\n$str")
        println("response: $response")

        val dataParse = Json.decodeFromString<FbMapData>(str)

        println(dataParse)

//        val korgeParse = com.soywiz.korio.serialization.json.Json.parse(str)

//        val key1 = korgeParse.keys.first()
//        val value1 = korgeParse[key1]
//
//        println("key1: $key1")
//        println("value1: $value1")

//        println("korgeParse: $korgeParse")

//        val mapVal = Json.decodeFromString<Map<String, String>>(str)
//        println("mapVal: $mapVal")
        return Json.decodeFromString<FbMapData>(str)
    }

    suspend fun getData2(): HttpClient.Response {
        return client.request(
            Http.Method.GET,
            //            "https://xenotactic-default-rtdb.firebaseio.com/users.json",
            "https://xenotactic-default-rtdb.firebaseio.com/maps/a6c00d68a95a5043374c1017b982d8f0b3009179/data/003_teleportIns.json",
        )
    }

    suspend fun getData(): HttpClient.Response {
        return client.request(
            Http.Method.GET,
            "https://xenotactic-default-rtdb.firebaseio.com/users.json?access_token=$accessToken",
//            "https://xenotactic-default-rtdb.firebaseio.com/mike/bob/timestamp.json?access_token=$accessToken",
        )
    }

    suspend fun headData(): HttpClient.Response {
        return client.request(
            Http.Method.HEAD,
            "https://xenotactic-default-rtdb.firebaseio.com/mike/bob/timestamp.json",
        )
    }

    suspend fun putData(): Any {
        return restClient.put(
            "users/bob/name.json?access_token=$accessToken",
            """
                { "first": "Jack", "last": "Sparrow" }
            """.trimIndent()
        )
    }

    suspend fun putData2(): Any {
        return client.request(
            Http.Method.PUT,
            "https://xenotactic-default-rtdb.firebaseio.com/users/mike/name.json?access_token=$accessToken",
            content =             """
                { "first": "Jack", "last": "Sparrow", "timestamp": 0 }
            """.trimIndent().openAsync()
        )
    }

    suspend fun putData3(): Any {
        return client.request(
            Http.Method.PUT,
            "https://xenotactic-default-rtdb.firebaseio.com/users/mike/name/timestamp.json",
            content =             """
                { ".sv": "timestamp"}
            """.trimIndent().openAsync()
        )
    }

}

@JvmInline
value class JsonString(val str: String)

@JvmInline
value class RawString(val str: String)