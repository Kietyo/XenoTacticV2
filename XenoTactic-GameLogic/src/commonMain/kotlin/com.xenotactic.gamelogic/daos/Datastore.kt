package com.xenotactic.gamelogic.daos

import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import kotlinx.coroutines.GlobalScope

class Datastore {
    val client = HttpClient()
    suspend fun getData() {
        val job = launch(GlobalScope.coroutineContext) {
            val response = client.get<HttpResponse>("https://xenotactic-default-rtdb.firebaseio" +
                    ".com/users.json") {
//                this.setAttributes {
//                    this.put<String>(AttributeKey("print"), "silent")
//                }
            }
            println(response)
            println(response.readText())
        }
        job.join()
    }

    suspend fun putData(): String {
//         { ".sv" : "timestamp" , "first": "Jack", "last": "Sparrow" }
        return client.put<String> {
            url("https://xenotactic-default-rtdb.firebaseio.com/users2/cabba/timestamp.json")
            body = """
                    { ".sv_millis" : "timestamp" }
                """.trimIndent()
        }
    }
}