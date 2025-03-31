package com.dinesh.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResult(
    val name: String,
    val age:Int
)
