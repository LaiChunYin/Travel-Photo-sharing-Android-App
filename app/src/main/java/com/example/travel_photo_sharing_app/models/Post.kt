package com.example.travel_photo_sharing_app.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.time.LocalDateTime

class Post(
    val address: String,
    val type: String,
//    val author: User,
    val authorEmail: String,
    val description: String,
    val visibleToGuest: Boolean,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
    val createdAt: String = LocalDateTime.now().toString(),
    val idFromDb: String? = null
): Serializable {

    constructor(document: DocumentSnapshot): this(
        address = document.getString("address") ?: "No Address",
        type = document.getString("type") ?: "No Type",
        authorEmail = document.getString("authorEmail") ?: "No Author Email",
        description = document.getString("description") ?: "No Description",
        visibleToGuest = document.getBoolean("visibleToGuest") ?: false,
        latitude = document.getDouble("latitude") ?: 0.0,
        longitude = document.getDouble("longitude") ?: 0.0,
        imageUrl = document.getString("imageUrl"),
        createdAt = document.getString("createdAt") ?: LocalDateTime.now().toString(),
        idFromDb = document.id


//        document["address"] as String,
//        document["type"] as String,
//        document["authorEmail"] as String,
//        document["description"] as String,
//        document["visibleToGuest"] as Boolean,
//        document["latitude"] as Double,
//        document["longitude"] as Double,
//        document["imageUrl"] as String,
//        document["createdAt"] as String
    )
    {
        Log.d("Post", "using constructor ${this}")
    }
    fun matchesQuery(query: String): Boolean {
        val lowerCaseQuery = query.lowercase()

        val matchFound = type.lowercase().contains(lowerCaseQuery) ||
                description.lowercase().contains(lowerCaseQuery) ||
//                author.username.lowercase().contains(lowerCaseQuery) ||
                authorEmail.lowercase().contains(lowerCaseQuery) ||
                address.lowercase().contains(lowerCaseQuery) ||
                matchesNumericQuery(lowerCaseQuery)

        return matchFound
    }

    override fun toString(): String {
        return "Post(${idFromDb}) is $address, $type, $description, $imageUrl"
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Post) return false
//        if(this.postalCode != other.postalCode) return false
        if(this.address != other.address) return false

        return true
    }
    private fun matchesNumericQuery(query: String): Boolean {
        val queryAsNumber = query.toIntOrNull()
        return queryAsNumber != null
//                &&
//                (
//                numOfBedrooms == queryAsNumber ||
//                numOfKitchens == queryAsNumber ||
//                numOfBathrooms == queryAsNumber)
    }
    fun toHashMap(): HashMap<String, Any?>{
        val result = HashMap<String, Any?>()

        result["address"] = address
        result["type"] = type
        result["authorEmail"] = authorEmail
        result["description"] = description
        result["visibleToGuest"] = visibleToGuest
        result["latitude"] = latitude
        result["longitude"] = longitude
        result["imageUrl"] = imageUrl
        result["createdAt"] = createdAt

        return result
    }
}
