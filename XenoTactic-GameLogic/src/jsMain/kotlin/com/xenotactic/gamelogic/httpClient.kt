package com.xenotactic.gamelogic

import io.ktor.client.*
import io.ktor.client.engine.js.*

actual fun httpClient(): HttpClient {
    return HttpClient(Js)
}