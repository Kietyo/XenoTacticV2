package com.xenotactic.server.dao

import io.ktor.client.*
import io.ktor.client.engine.cio.*

class Datastore(val accessToken: String = "") {
    val client = HttpClient(CIO)

    fun putMap() {

    }
}