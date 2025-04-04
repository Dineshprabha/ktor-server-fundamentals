package com.dinesh.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable


fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_sessions") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 300
        }
    }
}

@Serializable
data class UserSession(
    val username : String
)