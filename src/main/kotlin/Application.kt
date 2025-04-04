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

    val jwt = environment.config.config("ktor.jwt")
    val realm = jwt.property("realm").getString()
    val secret = jwt.property("secret").getString()
    val issuer = jwt.property("issuer").getString()
    val audience = jwt.property("audience").getString()
    val tokenExpiry = jwt.property("expiry").getString().toLong()

    val config = JWTConfig(
        realm = realm,
        audience = audience,
        issuer = issuer,
        tokenExpiry = tokenExpiry,
        secret = secret

    )


    configureResources()
//    configureBasicAuthentication()
//    configureDigestAuthentication()
//    configureBearerAuthentication()
    configureSessions()
//    configureSessionAuthentication()
    configureJWTAuthentication(config)
    configureSSE()
    configureWebsockets()
    configureRouting(usersDataSource, config)
    configureSerialization()
    configureStatusPages()
    configureRequestValidation()

}
