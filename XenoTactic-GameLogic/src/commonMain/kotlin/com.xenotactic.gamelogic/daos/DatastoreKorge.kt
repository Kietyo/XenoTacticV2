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

    suspend fun getData(): HttpClient.Response {
        return client.request(
            Http.Method.GET,
            "https://xenotactic-default-rtdb.firebaseio.com/users.json",
        )
    }

    suspend fun headData(): HttpClient.Response {
        return client.request(
            Http.Method.HEAD,
            "https://xenotactic-default-rtdb.firebaseio.com/users.json",
        )
    }

    suspend fun putData(): Any {

        val response = restClient.put(
            "users/bob/name.json",
            """
                { "first": "Jack", "last": "Sparrow" }
            """.trimIndent()
        )
        return response
    }

    suspend fun putData2(): Any {
        return client.request(
            Http.Method.PUT,
            "https://xenotactic-default-rtdb.firebaseio.com/users/mike/name.json",
            content =             """
                { "first": "Jack", "last": "Sparrow" }
            """.trimIndent().openAsync()
        )
    }


}

@JvmInline
value class JsonString(val str: String)

@JvmInline
value class RawString(val str: String)