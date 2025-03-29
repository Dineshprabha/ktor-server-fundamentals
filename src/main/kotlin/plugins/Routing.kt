package com.dinesh.plugins

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File
import java.io.FileOutputStream

fun Application.configureRouting() {


    routing {


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
    }
}
