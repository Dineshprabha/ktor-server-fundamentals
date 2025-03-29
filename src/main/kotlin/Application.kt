package com.dinesh

import com.dinesh.plugins.configureResources
import com.dinesh.plugins.configureRouting
import com.dinesh.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureResources()
    configureRouting()
    configureSerialization()
}
