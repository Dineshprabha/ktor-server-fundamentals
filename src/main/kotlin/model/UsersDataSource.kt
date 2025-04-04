package com.dinesh.model

import com.dinesh.ktor_mongodb.MongoDatabaseFactory
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.logging.Filter

class UsersDataSource {

    private val db = MongoDatabaseFactory.db
    private val usersCollection = db.getCollection<UserEntity>("users")


    suspend fun insertOneUser(entity: UserEntity) : Boolean {
        return usersCollection.insertOne(entity).wasAcknowledged()
    }

    suspend fun insertMultipleUser(entities : List<UserEntity>) : Boolean {
        return usersCollection.insertMany(entities).wasAcknowledged()
    }

    suspend fun getUserById(id: String) : User? {
        val filter = Filters.eq("_id", id)
        val result = usersCollection.find(filter).firstOrNull()
        return result?.toUser()
    }

    fun filterUsers(age:Int) : Flow<User>{
        val filter = Filters.gt("age",age)
        val result = usersCollection.find(filter).map { it.toUser() }
        return result
    }

    fun getAllUsers() : Flow<User> {
        val filter = Filters.empty()
        return usersCollection.find(filter).map { it.toUser() }
    }

    fun getAllUsers1() : Flow<UserResult> {
        val filter = Filters.empty()

        val projections = Projections.fields(
            Projections.include("name", "age"),
            Projections.excludeId()
        )

        val result = usersCollection.withDocumentClass<UserResult>().find(filter).projection(projections)
        return result
    }
}