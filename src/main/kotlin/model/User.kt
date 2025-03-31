package com.dinesh.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String?= null,
    val name: String,
    val email: String,
    val profession: String,
    val age: Int,
    val country: String
) {
    fun toUserEntity() : UserEntity {
        return UserEntity (
            name = name,
            email =  email,
            profession = profession,
            age = age,
            country = country
        )
    }
}