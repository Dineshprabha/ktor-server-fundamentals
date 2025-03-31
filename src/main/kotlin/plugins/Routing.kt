package com.dinesh.plugins

import com.dinesh.model.Product
import com.dinesh.model.User
import com.dinesh.model.UserEntity
import com.dinesh.model.UsersDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

fun Application.configureRouting(usersDataSource: UsersDataSource) {

    routing {

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


        //part-4

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
    }
}
