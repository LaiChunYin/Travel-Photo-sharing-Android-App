package com.example.travel_photo_sharing_app.models

import java.io.Serializable

open class User(
    val username: String,
    val password: String,
    val userType: String,
    val savedPosts: MutableList<Post> = mutableListOf(),
    val email: String,
    val phone: String,
): Serializable {
    override fun toString(): String {
        return "User is ($username, $password, $userType, $savedPosts)"
    }
}