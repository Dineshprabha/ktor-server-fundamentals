package com.dinesh.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<String>() { body ->
            if(body.isBlank()) ValidationResult.Invalid("Message cannot be empty")
            else ValidationResult.Valid
        }
    }
}