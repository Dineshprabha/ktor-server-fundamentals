package com.dinesh.plugins

import com.dinesh.model.Product
import com.dinesh.model.User
import com.dinesh.model.UserEntity
import com.dinesh.model.UsersDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sse.*
import io.ktor.server.websocket.*
import io.ktor.sse.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap

fun Application.configureRouting(usersDataSource: UsersDataSource, config: JWTConfig) {

    val usersDB = mutableMapOf<String, String>()
    val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    routing {

        /*-------------------------------------------------- Ktor with mongo database ----------------------------------------------------*/
        get("users") {
            val id = call.queryParameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val user = usersDataSource.getUserById(id) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(user)
        }


        get("users/filter") {
            val age = call.queryParameters["age"] ?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val users = usersDataSource.filterUsers(age)
            call.respond(users)
        }

        post("user") {
            val user = call.receive<User>()
            val entity = user.toUserEntity()

            val result = usersDataSource.insertOneUser(entity)
            call.respond(mapOf("success " to result))
        }

        post("users") {
            val users = call.receive<List<User>>()
            val entities = users.map { it.toUserEntity() }

            val result = usersDataSource.insertMultipleUser(entities)
            call.respond(mapOf("success " to result))
        }

        post("usersFromFile") {

            val path = "dummy-data/users.json"
            val jsonString = File(path).readText()
            val users : List<User> = Json.decodeFromString(jsonString)


            val entities = users.map { it.toUserEntity() }
            val result = usersDataSource.insertMultipleUser(entities)
            call.respond(mapOf("success " to result))
        }


        get("allUsers") {
            val users = usersDataSource.getAllUsers()
            call.respond(users)
        }


        get("allUsers1") {
            val users = usersDataSource.getAllUsers1()
            call.respond(users)
        }


        /*-------------------------------------------------- server fundamentals ----------------------------------------------------*/

        post("greet") {
            val name = call.receiveText()
            call.respondText("Hello, $name")
        }

        post("channel") {
            val channel = call.receiveChannel()
            val text = channel.readRemaining().readText()
            call.respondText(text)
        }

        post("uploads") {
            val file = File("uploads/sample2.jpg").apply {
                parentFile.mkdirs()
            }

 //ByteArray will be inefficient for the large file because its consumes more memory and it takes time

            val byteArray = call.receive<ByteArray>()
            file.writeBytes(byteArray)


            //Using Stream

            val stream = call.receiveStream()
            FileOutputStream(file).use { outputstream ->
                stream.copyTo(outputstream, bufferSize = 16*1024)
            }


            //using Channel

            val channel = call.receiveChannel()
            channel.copyAndClose(file.writeChannel())


            call.respondText("File upload success")
        }


        post("product") {
            val product = call.receiveNullable<Product>() ?: return@post call.respond(HttpStatusCode.BadRequest)
            call.respond(product)
        }


        /*-------------------------------------------------- part - 4 ----------------------------------------------------*/

        post("checkout") {
            val formData = call.receiveParameters()
            val productId = formData["productId"]
            val quantity = formData["quantity"]
            call.respondText("Order placed successfully, Product Id : $productId & Quantity : $quantity")
        }



        post("product1") {
//            throw Exception("Database failed to in initialize")
            call.respond(HttpStatusCode.Unauthorized )
        }


        /*-------------------------------------------------- Part - 6 Request Validation ----------------------------------------------------*/


        post("message") {
            val message = call.receive<String>()
            call.respondText(message)
        }


        staticResources("static", "static") {
        }


        /*-------------------------------------------------- Part - 10 Basic Authentication ----------------------------------------------------*/

//
//        authenticate ("session-auth") {
//            get("") {
//                val username = call.principal<UserSession>()?.username
//                call.respondText("Hello, $username")
//            }
//        }

        authenticate ("jwt-auth") {
            get("") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString()
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username ! the token expires after $expiresAt ms.")
            }
        }


        post("signup") {
            val requestData = call.receive<AuthRequest>()
            if(usersDB.containsKey(requestData.username)){
                call.respondText("User already exists")
            }else {
                usersDB[requestData.username] = requestData.password

                //for session authentication
//                call.sessions.set(UserSession(requestData.username))
//                call.respondText("User signup success")

                //for jwt authentication
                val token = generateToken(config = config, username = requestData.username)
                call.respond(mapOf("token" to token))

            }
        }

        post("login") {
            val requestData = call.receive<AuthRequest>()
            val storedPassword = usersDB[requestData.username]
                ?: return@post call.respondText("User doesn't exist")

            if (storedPassword == requestData.password) {

                //for session authentication
//                call.sessions.set(UserSession(requestData.username))
//                call.respondText("Login Success")


                //for jwt authentication

                val token = generateToken(config = config, username = requestData.username)
                call.respond(mapOf("token" to token))

            }else{
                call.respondText("Invalid credentials")
            }

        }

        post("logout") {
            call.sessions.clear<UserSession>()
            call.respondText("Logout Success")
        }

        /*---------------------------------SSE-----------------*/


        sse ("events"){
            repeat(8) {
                send(ServerSentEvent("Event: ${ it + 1}"))
                delay(1000L)
            }
        }


        webSocket ("chat") {
            val username = call.request.queryParameters["username"] ?: run {
                this.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "username is required for establishing connection"))
                return@webSocket
            }

            onlineUsers[username] = this
            send("You are connected!!")
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val message = Json.decodeFromString<Message>(frame.readText())
                        if (message.to.isNullOrBlank()){
                            onlineUsers.values.forEach {
                                it.send("$username : ${message.text}")
                            }
                        }else{
                            val session = onlineUsers[message.to]
                            session?.send("$username : ${message.text}")
                        }

                    }
                }

            }finally {
                onlineUsers.remove(username)
                this.close( )

            }
        }
    }


}

@Serializable
data class Message(
    val text: String,
    val to: String ? = null
)

@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)
