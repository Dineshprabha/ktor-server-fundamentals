package com.dinesh.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*

fun Application.configureBasicAuthentication() {

    val hashedUserTable = createHashedUserTable()

    install(Authentication) {
        basic ("basic-auth"){
                validate { credential ->
//                    val username = credential.name
//                    val password = credential.password
//
//                    if (username == "admin" && password == "password") {
//                        UserIdPrincipal(username)
//                    }else {
//                        null
//                    }

                    hashedUserTable.authenticate(credential)

                }
        }
    }
}

fun createHashedUserTable () : UserHashedTableAuth {

    val digestFuntion = getDigestFunction("SHA-256") { "ktor${it.length}" }
    return UserHashedTableAuth(
        digester = digestFuntion,
        table = mapOf(
            "admin" to digestFuntion("password"),
            "dinesh" to digestFuntion("prabha")
        )
    )
}