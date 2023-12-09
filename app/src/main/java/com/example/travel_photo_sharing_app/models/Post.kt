package com.example.travel_photo_sharing_app.models

import java.io.Serializable

class Post(
    val address: String,
    val type: String,
    val author: User,
    val description: String,
    val visibleToGuest: Boolean,
    val imageUrl: String? = null
): Serializable {
    fun matchesQuery(query: String): Boolean {
        val lowerCaseQuery = query.lowercase()

        val matchFound = type.lowercase().contains(lowerCaseQuery) ||
                description.lowercase().contains(lowerCaseQuery) ||
                author.username.lowercase().contains(lowerCaseQuery) ||
                address.lowercase().contains(lowerCaseQuery) ||
                matchesNumericQuery(lowerCaseQuery)

        return matchFound
    }

    override fun toString(): String {
        return "Post is $address, $type, $description, $imageUrl"
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
}
