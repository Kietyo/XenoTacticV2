package com.xenotactic.client

import io.ktor.client.*

actual fun httpClient(): HttpClient {
    return HttpClient()
}