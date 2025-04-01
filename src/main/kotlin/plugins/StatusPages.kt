package com.dinesh.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun Application.configureStatusPages() {

    install(StatusPages) {

        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("errors" to cause.reasons))

        }


//        exception<Throwable> {call, cause ->
//            call.respondText("500: ${cause.message}", status = HttpStatusCode.InternalServerError)
//
//        }

//        status(HttpStatusCode.Unauthorized) { call, cause ->
//            call.respondText("401: You are not authorized to access")
//        }
    }

}