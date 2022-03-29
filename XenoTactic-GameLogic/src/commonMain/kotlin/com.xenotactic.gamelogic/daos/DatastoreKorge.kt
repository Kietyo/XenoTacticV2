package com.xenotactic.gamelogic.daos

import com.soywiz.korio.net.http.*
import com.soywiz.korio.stream.openAsync
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmInline

class DatastoreKorge {
    val restClient =
        createHttpClientEndpoint("https://xenotactic-default-rtdb.firebaseio.com").rest()
    val client = createHttpClient {
    }

    init {

    }

    suspend fun addMapIfNotExists() {

    }

    suspend fun getData(): HttpClient.Response {
        return client.request(
            Http.Method.GET,
//            "https://xenotactic-default-rtdb.firebaseio.com/users.json",
            "https://xenotactic-default-rtdb.firebaseio.com/mike/bob/timestamp.json",
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
            "users/bob/name.json",
            """
                { "first": "Jack", "last": "Sparrow" }
            """.trimIndent()
        )
    }

    suspend fun putData2(): Any {
        return client.request(
            Http.Method.PUT,
            "https://xenotactic-default-rtdb.firebaseio.com/users/mike/name.json",
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