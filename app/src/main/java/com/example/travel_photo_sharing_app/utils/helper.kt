package com.example.travel_photo_sharing_app.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.travel_photo_sharing_app.models.User
import com.google.gson.Gson
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.repositories.PostRepository
import com.example.travel_photo_sharing_app.repositories.UserRepository
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

lateinit var sharedPreferences: SharedPreferences
lateinit var prefEditor: SharedPreferences.Editor
val tag = "Utils"
//fun getLoggedInUser(context: AppCompatActivity): User? {
//    val loggedInUserName = context.intent.getStringExtra("USER") ?: ""
//    Log.i("helper", "in getloggedInUser: ${loggedInUserName}")
//    if(loggedInUserName == "") return null
//    // configure shared preferences
//    sharedPreferences = context.getSharedPreferences("USERS",
//        AppCompatActivity.MODE_PRIVATE
//    )
//    return Gson().fromJson(sharedPreferences.getString(loggedInUserName, ""), User::class.java)
//}
//
//fun getLoggedInUser(loggedInUserName: String, context: Context): User? {
//    if(loggedInUserName == "") return null
//    // configure shared preferences
//    sharedPreferences = context.getSharedPreferences("USERS",
//        AppCompatActivity.MODE_PRIVATE
//    )
//    return Gson().fromJson(sharedPreferences.getString(loggedInUserName, ""), User::class.java)
//}

fun saveDataToSharedPref(context: Context, file: String, key: String, data: Any, toJson: Boolean = false) {
    var dataString: String

    // configure shared preferences
    sharedPreferences = context.getSharedPreferences(file,
        AppCompatActivity.MODE_PRIVATE
    )
    prefEditor = sharedPreferences.edit()

    if(toJson){
        dataString = Gson().toJson(data)
    }
    else {
        dataString = data.toString()
    }
    prefEditor.putString(key, dataString)
    prefEditor.apply()
}

fun getAllUserPosts(context: Context): MutableList<Post> {
    val allPosts = mutableListOf<Post>()
    sharedPreferences = context.getSharedPreferences("POSTS", AppCompatActivity.MODE_PRIVATE)

    if(sharedPreferences.all.isNotEmpty()) {
        val gson = Gson()
        for(posts in sharedPreferences.all.values){
            val userPosts = gson.fromJson<List<Post>>(posts as String, object : TypeToken<List<Post>>() {}.type)
            allPosts.addAll(userPosts)
        }

    }
    Log.i(tag, "all posts: ${allPosts}")
    return allPosts
}



fun checkDuplicatedPost(newPost: Post, context: Context): Boolean {
    val allPost = getAllUserPosts(context)
    for(post in allPost){
        if(newPost.equals(post)) return true
    }
    return false

}

//suspend fun initializePosts(context: Context): MutableList<Post> {
//suspend fun initializePosts(context: Context): MutableLiveData<List<Post>> {
fun initializePosts(context: Context) {
    val postsToBeDisplay = mutableListOf<Post>()
    val sampleUser = User("johndoe@example.com", "123", "John Doe", mutableListOf(), mutableListOf())
    val email = sampleUser.email
    val postRepository = PostRepository()

    val samplePosts = mutableListOf(
        Post(
            type = "Condo",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Modern condo with 2 bedrooms and a great view of the city.",
//            numOfBedrooms = 2,
//            numOfKitchens = 1,
//            numOfBathrooms = 2,
            address = "123 Main St, Metropolis",
//            city = "Metropolis",
//            postalCode = "12345",
//            availableForRent = true,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "condo",
//                imageUrl = "https://thumbor.forbes.com/thumbor/fit-in/1290x/https://www.forbes.com/advisor/wp-content/uploads/2022/10/condo-vs-apartment.jpeg.jpg"
        ),
        Post(
            type = "House",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Spacious house with a large backyard and modern amenities.",
//            numOfBedrooms = 4,
//            numOfKitchens = 1,
//            numOfBathrooms = 3,
            address = "456 Maple Ave, Springfield",
//            city = "Springfield",
//            postalCode = "67890",
//            availableForRent = true,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "house"
//                imageUrl = "https://www.trulia.com/pictures/thumbs_5/zillowstatic/fp/6fb8604d0f16bf8c22ea266d19bc7ccf-full.webp"
        ),
        Post(
            type = "Apartment",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Cozy apartment close to downtown and public transportation.",
//            numOfBedrooms = 1,
//            numOfKitchens = 1,
//            numOfBathrooms = 1,
            address = "789 River Rd, Riverdale",
//            city = "Riverdale",
//            postalCode = "10111",
//            availableForRent = false,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "apartment"
//                imageUrl = "https://www.trulia.com/pictures/thumbs_6/zillowstatic/fp/5aa7dd68c2f6536683aaac7ad6f9b99d-full.webp"
        ),
        Post(
            type = "House",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Cozy suburban house with a beautiful garden and a family-friendly neighborhood.",
//            numOfBedrooms = 3,
//            numOfKitchens = 1,
//            numOfBathrooms = 2,
            address = "202 Maple Lane, Pleasantville",
//            city = "Pleasantville",
//            postalCode = "30002",
//            availableForRent = false,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "house_2"
//            imageUrl = "https://www.bankrate.com/2022/07/20093642/what-is-house-poor.jpg?auto=webp&optimize=high&crop=16:9&width=912"
        ),
        Post(
            type = "Apartment",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Stylish downtown apartment with easy access to nightlife and public transport.",
//            numOfBedrooms = 2,
//            numOfKitchens = 1,
//            numOfBathrooms = 1,
            address = "303 City Center Ave, Metropolis",
//            city = "Metropolis",
//            postalCode = "40003",
//            availableForRent = true,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "apartment_2"
//            imageUrl = "https://pqnk.com/wp-content/uploads/2021/07/SW-perspective-50-percent-transparency.png"
        ),
        Post(
            type = "House",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Traditional house in a historical neighborhood, featuring a large porch and backyard.",
//            numOfBedrooms = 4,
//            numOfKitchens = 1,
//            numOfBathrooms = 3,
            address = "404 Heritage Road, Oldtown",
//            city = "Oldtown",
//            postalCode = "50004",
//            availableForRent = true,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "house_2"
//            imageUrl = "https://www.zolo.ca/blog/wp-content/uploads/2018/03/large-home-with-vibrant-landscaping-1024x576.jpeg"
        ),
        Post(
            type = "Condo",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Modern waterfront condo with panoramic ocean views and high-end finishes.",
//            numOfBedrooms = 2,
//            numOfKitchens = 1,
//            numOfBathrooms = 2,
            address = "505 Seaside Blvd, Bay City",
//            city = "Bay City",
//            postalCode = "60005",
//            availableForRent = false,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "condo_3"
//            imageUrl = "https://blog.remax.ca/wp-content/uploads/sites/8/2019/02/condo-690x518.jpg?resize=600,450"
        ),
        Post(
            type = "Apartment",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Compact and efficient apartment in a vibrant and trendy neighborhood.",
//            numOfBedrooms = 1,
//            numOfKitchens = 1,
//            numOfBathrooms = 1,
            address = "606 Downtown Ave, Night City",
//            city = "Night City",
//            postalCode = "70006",
//            availableForRent = true,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "apartment_3"
//            imageUrl = "https://wp-cpr.s3.amazonaws.com/uploads/2023/07/Creekwalk-Apartments.png?resize=2064,1376"
        ),
        Post(
            type = "House",
            // author = sampleUser,
            authorEmail = email,
//            owner = sampleOwner,
            description = "Spacious country house with a large garden and serene natural surroundings.",
//            numOfBedrooms = 5,
//            numOfKitchens = 1,
//            numOfBathrooms = 4,
            address = "707 Countryside Lane, Greenfield",
//            city = "Greenfield",
//            postalCode = "80007",
//            availableForRent = true,
            visibleToGuest = true,
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "house_3"
//            imageUrl = "https://i.pinimg.com/564x/1a/16/2c/1a162ca2f756d8417c6e8cc977bc7fbc.jpg"
        )

    )

    // show the posts added by the users
    sharedPreferences = context.getSharedPreferences("POSTS", AppCompatActivity.MODE_PRIVATE)
    val allUserPosts = getAllUserPosts(context)
    Log.i(tag, "allMyPosts: ${allUserPosts}")
    postsToBeDisplay.addAll(samplePosts)

    for(post in samplePosts){
        postRepository.addPost(post)
    }

//    postsToBeDisplay.addAll(allUserPosts)
//    return postsToBeDisplay
}

fun getCategorySpinnerList(context: Context): MutableList<String>{
    val categoryOptions = context.resources.getStringArray(R.array.category_options);
    Log.d(tag, "options are ${categoryOptions}")
    return categoryOptions.toMutableList()
}

fun testing(){

}