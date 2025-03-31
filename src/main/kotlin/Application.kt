package com.dinesh

import com.dinesh.ktor_mongodb.MongoDatabaseFactory
import com.dinesh.model.UsersDataSource
import com.dinesh.plugins.configureResources
import com.dinesh.plugins.configureRouting
import com.dinesh.plugins.configureSerialization
import com.dinesh.plugins.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val usersDataSource = UsersDataSource()


    configureResources()
    configureRouting(usersDataSource)
    configureSerialization()
    configureStatusPages()
}
