package com.xenotactic.gamelogic.daos

import korlibs.io.async.launch
import korlibs.io.async.runBlockingNoJs
import com.soywiz.korio.net.http.Http
import com.soywiz.korio.stream.openAsync
import com.xenotactic.gamelogic.httpClient
import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.gamelogic.model.GameMap
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Datastore(val accessToken: String = "") {
    val client = httpClient()
    suspend fun getData(): String {
        return client.get("https://xenotactic-default-rtdb.firebaseio.com/users.json?access_token=$accessToken") {
            //                this.setAttributes {
            //                    this.put<String>(AttributeKey("print"), "silent")
            //                }
            Unit
        }.bodyAsText()
    }

    suspend fun mapExists(mapId: String): Boolean {
        return client.request(
            "https://xenotactic-default-rtdb.firebaseio" +
                    ".com/maps/$mapId/timestamp.json?access_token=$accessToken"
        ).bodyAsText() != "null"
    }


    suspend fun addMapIfNotExists(map: GameMap) {
        val id = MapToId.calculateId(map)
        if (mapExists(id)) {
            return
        }
        val fbMap = map.toFbGameMap()
        client.put(
            "https://xenotactic-default-rtdb.firebaseio.com/maps/$id/data" +
                    ".json?access_token=$accessToken"
        ) {
            this.setBody(Json.encodeToString(fbMap))
        }
        client.put("https://xenotactic-default-rtdb.firebaseio.com/maps/$id/timestamp.json?access_token=$accessToken") {
            this.setBody("""{ ".sv": "timestamp"}""")
        }
    }

//        suspend fun putData(): String {
//            //         { ".sv" : "timestamp" , "first": "Jack", "last": "Sparrow" }
//            return client.put<String> {
//                url("https://xenotactic-default-rtdb.firebaseio.com/users2/cabba/timestamp.json")
//                body = """
//                        { ".sv_millis" : "timestamp" }
//                    """.trimIndent()
//            }
//        }
}