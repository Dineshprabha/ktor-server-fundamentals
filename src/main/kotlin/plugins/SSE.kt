package com.dinesh.plugins

import io.ktor.server.application.*
import io.ktor.server.sse.*

fun Application.configureSSE(){
    install(SSE){

    }
}