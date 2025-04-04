package com.dinesh.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.security.MessageDigest


const val Realm = "Access Protected routes"

//this is the format after the MD5 alogorithm hased
//username:realm:password

val userTable : Map<String, ByteArray> = mapOf(
    "admin" to getMD5Digest("admin:${Realm}:password"),
    "dinesh" to getMD5Digest("user:${Realm}:prabha")
)


fun getMD5Digest(value: String) : ByteArray {
    return MessageDigest
        .getInstance("MD5")
        .digest(value.toByteArray())
}
fun Application.configureDigestAuthentication() {
    install(Authentication) {
        digest ("digest-auth") {
            realm = Realm

            digestProvider { userName, realm ->
                userTable[userName]
            }

            validate { credentials ->
                if (credentials.userName.isBlank()) {
                    UserIdPrincipal(credentials.userName)
                }else {
                    null
                }
            }
        }
    }
}