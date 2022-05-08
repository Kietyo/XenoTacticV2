package com.xenotactic.gamelogic

import io.ktor.client.*

actual fun httpClient(): HttpClient {
    return HttpClient()
}