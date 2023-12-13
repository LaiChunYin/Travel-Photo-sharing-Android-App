package com.example.travel_photo_sharing_app.screens

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.travel_photo_sharing_app.databinding.ActivityAddPostBinding
import com.example.travel_photo_sharing_app.models.Post
import com.example.travel_photo_sharing_app.models.User
import com.example.travel_photo_sharing_app.utils.checkDuplicatedPost
import com.example.travel_photo_sharing_app.utils.saveDataToSharedPref
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AddPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPostBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    private var loggedInUserName: String = ""
    val tag = "Add Post"

    var savedPosts: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        this.binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loggedInUserName = this.intent.getStringExtra("USER") ?: ""
        Log.i(tag, "In AddPost, user: ${loggedInUserName}")

        val selectedPost = intent.getSerializableExtra("POST_DATA") as Post?
        if (selectedPost != null) {
            binding.address.setText(selectedPost.address)
//            binding.city.setText(selectedPost.city)
//            binding.postalCode.setText(selectedPost.postalCode)
            binding.type.setText(selectedPost.type)
//            binding.authorName.setText(selectedPost.author.username)
//            binding.authorEmail.setText(selectedPost.author.email)
            binding.authorEmail.setText(selectedPost.authorEmail)

//            binding.authorPhone.setText(selectedPost.author.phone)
            binding.description.setText(selectedPost.description)
//            binding.bedrooms.setText(selectedPost.numOfBedrooms.toString())
//            binding.kitchens.setText(selectedPost.numOfKitchens.toString())
//            binding.bathrooms.setText(selectedPost.numOfBathrooms.toString())
            binding.visibleToGuest.isChecked = selectedPost.visibleToGuest
        }

        this.sharedPreferences = getSharedPreferences("POSTS", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        var resultsFromSP = sharedPreferences.getString(loggedInUserName, "")
        if (resultsFromSP != "") {
            val gson = Gson()
            val typeToken = object : TypeToken<List<Post>>() {}.type
            val tempPostList = gson.fromJson<List<Post>>(resultsFromSP, typeToken)

            savedPosts = tempPostList.toMutableList()
        }

        this.binding.saveBtn.setOnClickListener {
            this.saveData()
        }

        this.binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun saveData() {
        var address: String? = this.binding.address.text.toString()
        var latitude: Double? = this.binding.longitude.toString().toDouble()
        var longitude: Double? = this.binding.latitude.text.toString().toDouble()
        var type: String? = this.binding.type.text.toString()
//        var city: String? = this.binding.city.text.toString()
//        var postalCode: String? = this.binding.postalCode.text.toString()
//        var authorName: String? = this.binding.authorName.text.toString()
        var authorEmail: String? = this.binding.authorEmail.text.toString()
//        var authorPhone: String? = this.binding.authorPhone.text.toString()
        var desc: String? = this.binding.description.text.toString()
//        var bedrooms: Int? = if(this.binding.bedrooms.text.toString() != "") this.binding.bedrooms.text.toString().toInt() else null
//        var kitchens: Int? = if(this.binding.kitchens.text.toString() != "") this.binding.kitchens.text.toString().toInt() else null
//        var bathrooms: Int? = if(this.binding.bathrooms.text.toString() != "") this.binding.bathrooms.text.toString().toInt() else null
        var visibleToGuest: Boolean? = this.binding.visibleToGuest.isChecked

        // error check
        var hasEmptyField = false
        if (address.isNullOrEmpty()) {
            this.binding.address.setError("Address cannot be empty")
            hasEmptyField = true
        }
        if (latitude == null || latitude.isNaN()) {
            this.binding.latitude.setError("Latitude cannot be empty")
            hasEmptyField = true
        }
        if (longitude == null || longitude.isNaN()) {
            this.binding.longitude.setError("Longitude cannot be empty")
            hasEmptyField = true
        }
        if (type.isNullOrEmpty()) {
            this.binding.type.setError("Type cannot be empty")
            hasEmptyField = true
        }
//        if (city.isNullOrEmpty()) {
//            this.binding.city.setError("City cannot be empty")
//            hasEmptyField = true
//        }
//        if (postalCode.isNullOrEmpty()) {
//            this.binding.postalCode.setError("Postal code cannot be empty")
//            hasEmptyField = true
//        }
//        if (authorName.isNullOrEmpty()) {
//            this.binding.authorName.setError("Author name cannot be empty")
//            hasEmptyField = true
//        }
//        if (authorPhone.isNullOrEmpty()) {
//            this.binding.authorPhone.setError("Author phone cannot be empty")
//            hasEmptyField = true
//        }
//        if (authorEmail.isNullOrEmpty()) {
//            this.binding.authorEmail.setError("Author email cannot be empty")
//            hasEmptyField = true
//        }
        if (desc.isNullOrEmpty()) {
            this.binding.description.setError("Description cannot be empty")
            hasEmptyField = true
        }
//        if (bedrooms == null) {
//            this.binding.bedrooms.setError("Bedrooms cannot be empty")
//            hasEmptyField = true
//        }
//        if (kitchens == null) {
//            this.binding.kitchens.setError("Kitchens cannot be empty")
//            hasEmptyField = true
//        }
//        if (bathrooms == null) {
//            this.binding.bathrooms.setError("Bathrooms cannot be empty")
//            hasEmptyField = true
//        }
        if(hasEmptyField){
            Snackbar.make(binding.addPostParentLayout, "All fields are required.", Snackbar.LENGTH_LONG).show()
            return
        }

//        val author = User(authorName!!, authorEmail!!, authorPhone!!)
//        val author = User(authorName!!, authorEmail!!, "placeholder", mutableListOf(), "placeholder", authorPhone!!)
//        val author = User(authorEmail!!, )

        val selectedPost = intent.getSerializableExtra("POST_DATA") as Post?
        val index = intent.getIntExtra("INDEX", -1)
        Log.i(tag, "selected post ${index}: ${selectedPost}")

        // update post
        if(selectedPost != null){
            val postToEdit = Post(
                address!!,
//                city!!,
//                postalCode!!,
                type!!,
//                author,
                authorEmail!!,
                desc!!,
//                bedrooms!!,
//                kitchens!!,
//                bathrooms!!,
                visibleToGuest!!,
                latitude!!,
                longitude!!,
            )

//            if(checkDuplicatedPost(postToEdit, this)){
//                Snackbar.make(binding.addPostParentLayout, "Post already exist!!", Snackbar.LENGTH_LONG).show()
//                return
//            }
            savedPosts[index] = postToEdit
        }
        // create new post
        else {
//            var postToAdd = Post(address!!, city!!, postalCode!!, type!!, author, desc!!, bedrooms!!, kitchens!!, bathrooms!!, availableForRent!!)
//            var postToAdd = Post(address!!, type!!, author, desc!!, visibleToGuest!!)
            var postToAdd = Post(address!!, type!!, authorEmail!!, desc!!, visibleToGuest!!, latitude!!, longitude!!)
//            if(checkDuplicatedPost(postToAdd, this)){
//                Snackbar.make(binding.addPostParentLayout, "Post already exist!!", Snackbar.LENGTH_LONG).show()
//                return
//            }
            savedPosts.add(postToAdd)
        }
        saveDataToSharedPref(this, "POSTS", loggedInUserName, savedPosts, true)
        Snackbar.make(binding.addPostParentLayout, "Data Saved to SharedPrefs", Snackbar.LENGTH_LONG).show()
        finish()
    }

}