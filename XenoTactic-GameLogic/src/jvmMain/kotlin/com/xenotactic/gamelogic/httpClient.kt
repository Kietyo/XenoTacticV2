package com.xenotactic.gamelogic

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun httpClient(): HttpClient {
    return HttpClient(CIO)
}