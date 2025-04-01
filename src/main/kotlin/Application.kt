package com.dinesh

import com.dinesh.ktor_mongodb.MongoDatabaseFactory
import com.dinesh.model.UsersDataSource
import com.dinesh.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val usersDataSource = UsersDataSource()


    configureResources()
//    configureBasicAuthentication()
//    configureDigestAuthentication()
//    configureBearerAuthentication()
    configureSessions()
    configureSessionAuthentication()
    configureRouting(usersDataSource)
    configureSerialization()
    configureStatusPages()
    configureRequestValidation()

}
