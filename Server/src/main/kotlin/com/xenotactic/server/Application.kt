package com.xenotactic.server

import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.xenotactic.server.plugins.configureHTTP
import com.xenotactic.server.plugins.configureRouting
import com.xenotactic.server.plugins.configureSecurity

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureHTTP()
        configureRouting()
        configureSecurity()
    }.start(wait = true)
}
