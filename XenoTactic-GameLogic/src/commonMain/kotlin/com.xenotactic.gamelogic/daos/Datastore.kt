package com.xenotactic.gamelogic.daos

import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.dynamic.KDynamic.Companion.toChar
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*

class Datastore {
    val client = HttpClient()
    fun getData() {
        runBlockingNoJs {
            val response = client.get<HttpResponse>("https://xenotactic-default-rtdb.firebaseio" +
                    ".com/users.json") {
//                this.setAttributes {
//                    this.put<String>(AttributeKey("print"), "silent")
//                }
            }
            println(response)
            println(response.readText())
        }
    }

    fun putData() {
        runBlockingNoJs {
            val response = client.put<String> {
                url("https://xenotactic-default-rtdb.firebaseio.com/users/bob/name.json")
                body = """
                    { "first": "Jack", "last": "Sparrow" }
                """.trimIndent()
            }
            println(response)
        }

    }
}