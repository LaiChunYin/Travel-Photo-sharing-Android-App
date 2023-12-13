package com.example.travel_photo_sharing_app.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
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

        binding.tvLongtitude.visibility = View.GONE
        binding.tvLatitude.visibility = View.GONE

        loggedInUserName = this.intent.getStringExtra("USER") ?: ""
        Log.i(tag, "In AddPost, user: ${loggedInUserName}")

        binding.swAdressType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // If the switch is ON, make the address field invisible
                binding.address.visibility = View.GONE
                binding.tvLongtitude.visibility = View.VISIBLE
                binding.tvLatitude.visibility = View.VISIBLE
            } else {
                // If the switch is OFF, make the address field visible
                binding.address.visibility = View.VISIBLE
                binding.tvLongtitude.visibility = View.GONE
                binding.tvLatitude.visibility = View.GONE
            }
        }

        val selectedPost = intent.getSerializableExtra("POST_DATA") as Post?
        if (selectedPost != null) {
            binding.address.setText(selectedPost.address)
            binding.type.setText(selectedPost.type)
            binding.authorName.setText(selectedPost.author.username)
            binding.authorEmail.setText(selectedPost.author.email)
            binding.authorPhone.setText(selectedPost.author.phone)
            binding.description.setText(selectedPost.description)
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

        this.binding.btnUploadPhoto.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
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
        var type: String? = this.binding.type.text.toString()
        var authorName: String? = this.binding.authorName.text.toString()
        var authorEmail: String? = this.binding.authorEmail.text.toString()
        var authorPhone: String? = this.binding.authorPhone.text.toString()
        var desc: String? = this.binding.description.text.toString()
        var visibleToGuest: Boolean? = this.binding.visibleToGuest.isChecked

        // error check
        var hasEmptyField = false
        if (address.isNullOrEmpty()) {
            this.binding.address.setError("Address cannot be empty")
            hasEmptyField = true
        }
        if (type.isNullOrEmpty()) {
            this.binding.type.setError("Type cannot be empty")
            hasEmptyField = true
        }
        if (authorName.isNullOrEmpty()) {
            this.binding.authorName.setError("Author name cannot be empty")
            hasEmptyField = true
        }
        if (authorPhone.isNullOrEmpty()) {
            this.binding.authorPhone.setError("Author phone cannot be empty")
            hasEmptyField = true
        }
        if (authorEmail.isNullOrEmpty()) {
            this.binding.authorEmail.setError("Author email cannot be empty")
            hasEmptyField = true
        }
        if (desc.isNullOrEmpty()) {
            this.binding.description.setError("Description cannot be empty")
            hasEmptyField = true
        }
        if(hasEmptyField){
            Snackbar.make(binding.addPostParentLayout, "All fields are required.", Snackbar.LENGTH_LONG).show()
            return
        }

        val author = User(authorName!!, authorEmail!!, "placeholder", mutableListOf(), "placeholder", authorPhone!!)

        val selectedPost = intent.getSerializableExtra("POST_DATA") as Post?
        val index = intent.getIntExtra("INDEX", -1)
        Log.i(tag, "selected post ${index}: ${selectedPost}")

        // update post
        if(selectedPost != null){
            val postToEdit = Post(
                address!!,
                type!!,
                author,
                desc!!,
                visibleToGuest!!
            )

            savedPosts[index] = postToEdit
        }
        // create new post
        else {
            var postToAdd = Post(address!!, type!!, author, desc!!, visibleToGuest!!)
            if(checkDuplicatedPost(postToAdd, this)){
                Snackbar.make(binding.addPostParentLayout, "Post already exist!!", Snackbar.LENGTH_LONG).show()
                return
            }
            savedPosts.add(postToAdd)
        }
        saveDataToSharedPref(this, "POSTS", loggedInUserName, savedPosts, true)
        Snackbar.make(binding.addPostParentLayout, "Data Saved to SharedPrefs", Snackbar.LENGTH_LONG).show()
        finish()
    }

}