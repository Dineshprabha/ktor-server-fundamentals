package com.dinesh.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

val userDb : Map<String, String> = mapOf(
    "token1" to "user1",
    "token2" to "user2",
    "token3" to "user3",
    "token4" to "user4"
 )


fun Application.configureBearerAuthentication() {
    install(Authentication) {
        bearer ("bearer-auth") {
            realm = "Access tp protect routes"
            authenticate { tokenCredential ->
                val user = userDb[tokenCredential.token]
                if (!user.isNullOrBlank()){
                    UserIdPrincipal(user)
                }else{
                    null
                }
            }
        }
    }
}