package com.dinesh.ktor_mongodb

import com.mongodb.kotlin.client.coroutine.MongoClient

object MongoDatabaseFactory {


//    mongodb+srv://admin:admin@cluster0.ttdngm1.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0
    private val connectionString = System.getenv("MONGO_DB_URI")

    val db = MongoClient
        .create(connectionString)
        .getDatabase("ktor_mongodb")
}